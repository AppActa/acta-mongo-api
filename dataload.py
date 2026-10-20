import argparse
import json
import os
import re
import uuid
from collections import defaultdict
from datetime import datetime, timezone
from typing import Any
import psycopg
from faker import Faker
from pymongo import MongoClient


NAMESPACE = uuid.UUID("d86f1cc7-89cc-50bf-9cd0-ecbfe9a31af5")
ISHIKAWA_CATEGORIES = (
    "METODO",
    "MAO_DE_OBRA",
    "MAQUINA",
    "MATERIAL",
    "MEDICAO",
    "MEIO_AMBIENTE",
)
COLLECTIONS = (
    "ishikawas",
    "cinco_porques",
    "formularios",
    "respostas_formulario",
    "licoes_aprendidas",
    "relatorios",
)


def stable_uuid(kind: str, source_key: object) -> uuid.UUID:
    return uuid.uuid5(NAMESPACE, f"{kind}:{source_key}")


def postgres_dsn(value: str) -> str:
    return re.sub(r"^(postgres(?:ql)?\+[^:]+://)", "postgresql://", value)


def fetch_source_rows(dsn: str) -> tuple[list[dict[str, Any]], list[dict[str, Any]], list[dict[str, Any]]]:
    with psycopg.connect(postgres_dsn(dsn), options="-c default_transaction_read_only=on") as connection:
        with connection.cursor() as cursor:
            cursor.execute(
                """
                SELECT id, id_empresa, id_responsavel, id_ishikawa_mongo
                FROM pdca.ciclo
                WHERE id_ishikawa_mongo IS NOT NULL
                ORDER BY id
                """
            )
            cycles = [dict(zip((d.name for d in cursor.description), row)) for row in cursor.fetchall()]

            cursor.execute(
                """
                SELECT id, id_ciclo, titulo, descricao
                FROM pdca.problema
                ORDER BY id
                """
            )
            problems = [dict(zip((d.name for d in cursor.description), row)) for row in cursor.fetchall()]

            cursor.execute(
                """
                SELECT id, id_ciclo, id_problema, id_5_porques_mongo, descricao, principal
                FROM pdca.causa_raiz
                WHERE id_5_porques_mongo IS NOT NULL
                ORDER BY id
                """
            )
            root_causes = [dict(zip((d.name for d in cursor.description), row)) for row in cursor.fetchall()]
    return cycles, problems, root_causes


def make_documents(
    cycles: list[dict[str, Any]],
    problems: list[dict[str, Any]],
    root_causes: list[dict[str, Any]],
) -> dict[str, list[dict[str, Any]]]:
    documents: dict[str, list[dict[str, Any]]] = {name: [] for name in COLLECTIONS}
    problems_by_cycle: dict[int, list[dict[str, Any]]] = defaultdict(list)
    causes_by_cycle: dict[int, list[dict[str, Any]]] = defaultdict(list)
    for problem in problems:
        problems_by_cycle[problem["id_ciclo"]].append(problem)
    for cause in root_causes:
        causes_by_cycle[cause["id_ciclo"]].append(cause)

    for cycle in cycles:
        cycle_id = cycle["id"]
        mongo_ishikawa_id = uuid.UUID(str(cycle["id_ishikawa_mongo"]))
        cycle_problems = problems_by_cycle.get(cycle_id, [])
        cycle_causes = causes_by_cycle.get(cycle_id, [])
        principal_problem_ids = {
            cause["id_problema"] for cause in cycle_causes if cause.get("principal") and cause.get("id_problema") is not None
        }
        ordered_problems = sorted(
            cycle_problems,
            key=lambda problem: (problem["id"] not in principal_problem_ids, problem["id"]),
        )
        problem_parts = []
        for problem in ordered_problems:
            detail = str(problem.get("titulo") or "")
            description = str(problem.get("descricao") or "")
            text = ": ".join(value for value in (detail, description) if value)
            if text:
                role = "principal" if problem["id"] in principal_problem_ids else "outro"
                problem_parts.append(f"[{role}] {text}")
        problem_text = "; ".join(problem_parts) or "Problema do ciclo não informado no PostgreSQL"

        embedded_causes: list[dict[str, Any]] = []
        cause_ids: dict[int, uuid.UUID] = {}
        for cause in cycle_causes:
            cause_id = cause["id"]
            embedded_id = stable_uuid("ishikawa-causa", cause_id)
            cause_ids[cause_id] = embedded_id
            description = str(cause.get("descricao") or "Causa sem descrição no PostgreSQL")
            embedded_causes.append(
                {
                    "id": embedded_id,
                    "categoria": ISHIKAWA_CATEGORIES[cause_id % len(ISHIKAWA_CATEGORIES)],
                    "descricao": description,
                }
            )

        documents["ishikawas"].append(
            {
                "_id": mongo_ishikawa_id,
                "id_empresa": cycle["id_empresa"],
                "id_ciclo": cycle_id,
                "problema": str(problem_text),
                "causas": embedded_causes,
                "criado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                "atualizado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
            }
        )

        for cause in cycle_causes:
            mongo_five_whys_id = uuid.UUID(str(cause["id_5_porques_mongo"]))
            documents["cinco_porques"].append(
                {
                    "_id": mongo_five_whys_id,
                    "id_ishikawa": mongo_ishikawa_id,
                    "id_causa_ishikawa": cause_ids[cause["id"]],
                    "id_causa_raiz": cause["id"],
                    "hipotese": str(cause.get("descricao") or "Causa-raiz sem descrição no PostgreSQL"),
                    "porques": [],
                    "criado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                    "atualizado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                }
            )

        if cycle.get("id_responsavel") is None:
            continue
        faker = Faker("pt_BR")
        faker.seed_instance(int(cycle_id))
        form_id = stable_uuid("formulario-demo", cycle_id)
        question_yes_no = stable_uuid("pergunta-sim-nao", cycle_id)
        question_text = stable_uuid("pergunta-texto", cycle_id)
        documents["formularios"].append(
            {
                "_id": form_id,
                "id_empresa": cycle["id_empresa"],
                "id_ciclo": cycle_id,
                "titulo": "Checklist demonstrativo do ciclo",
                "descricao": "Registro sintético para validar a coleção de formulários.",
                "tipo": "CHECKLIST",
                "status": "ATIVO",
                "perguntas": [
                    {"id_pergunta": question_yes_no, "texto": "O plano foi acompanhado?", "tipo_resposta": "SIM_NAO", "obrigatoria": True, "opcoes": []},
                    {"id_pergunta": question_text, "texto": "Observação de demonstração", "tipo_resposta": "TEXTO", "obrigatoria": False, "opcoes": []},
                ],
                "id_ishikawa": mongo_ishikawa_id,
                "ids_usuarios_destinatarios": [cycle["id_responsavel"]],
                "publicado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                "criado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                "atualizado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
            }
        )
        documents["respostas_formulario"].append(
            {
                "_id": stable_uuid("resposta-demo", cycle_id),
                "id_empresa": cycle["id_empresa"],
                "id_ciclo": cycle_id,
                "id_formulario": form_id,
                "id_usuario": cycle["id_responsavel"],
                "respostas": [
                    {"id_pergunta": question_yes_no, "resposta": True},
                    {"id_pergunta": question_text, "resposta": "Resposta fictícia para demonstração."},
                ],
                "respondido_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
            }
        )
        documents["licoes_aprendidas"].append(
            {
                "_id": stable_uuid("licao-demo", cycle_id),
                "id_usuario": cycle["id_responsavel"],
                "id_empresa": cycle["id_empresa"],
                "id_ciclo": cycle_id,
                "titulo": "Lição demonstrativa do ciclo",
                "licao": faker.paragraph(nb_sentences=2),
                "tags": ["demonstração", "sintético"],
                "faseOrigem": "CHECK",
                "resultado": "Registro sintético gerado para validar a coleção.",
                "severidade": "BAIXA",
                "area": "Geral",
                "criado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                "atualizado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
            }
        )
        documents["relatorios"].append(
            {
                "_id": stable_uuid("relatorio-demo", cycle_id),
                "id_empresa": cycle["id_empresa"],
                "id_ciclo": cycle_id,
                "status": "RASCUNHO",
                "tipo": "CICLO_COMPLETO",
                "formato": "PDF",
                "titulo": "Relatório demonstrativo do ciclo",
                "resumo": "Conteúdo sintético para validar o modelo de relatório.",
                "conteudo": faker.paragraph(nb_sentences=4),
                "id_anexo": None,
                "publicado_em": None,
                "criado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
                "atualizado_em": datetime(2026, 1, 1, tzinfo=timezone.utc),
            }
        )
    return documents


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--apply",
        action="store_true",
        help="grava no MongoDB; sem esta opção, apenas exibe o preview",
    )
    args = parser.parse_args()
    pg_url = os.getenv("URL_SEGUNDO_BANCO")
    if not pg_url:
        parser.error("defina URL_SEGUNDO_BANCO com a conexão PostgreSQL usada no ambiente")
    if args.apply and not os.getenv("MONGODB_URI"):
        parser.error("--apply exige MONGODB_URI configurada no ambiente")

    cycles, problems, causes = fetch_source_rows(pg_url)
    documents = make_documents(cycles, problems, causes)

    counts = {name: len(items) for name, items in documents.items()}
    print(json.dumps({"mode": "apply" if args.apply else "preview", "collections": counts}, ensure_ascii=False, indent=2))
    if not args.apply:
        return 0

    with MongoClient(
        os.environ["MONGODB_URI"],
        uuidRepresentation="standard",
    ) as client:
        database = client.get_default_database()
        if database is None:
            parser.error("MONGODB_URI precisa incluir o nome do banco no caminho da URL")
        result: dict[str, dict[str, int]] = {}
        for collection_name, items in documents.items():
            inserted = 0
            existing = 0
            collection = database[collection_name]
            for item in items:
                outcome = collection.update_one({"_id": item["_id"]}, {"$setOnInsert": item}, upsert=True)
                if outcome.upserted_id is not None:
                    inserted += 1
                else:
                    existing += 1
            result[collection_name] = {"inserted": inserted, "already_present": existing}
        print(json.dumps({"mode": "apply", "database": database.name, "result": result}, ensure_ascii=False, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
