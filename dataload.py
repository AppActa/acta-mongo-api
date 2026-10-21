import os
from datetime import datetime, timezone
from uuid import NAMESPACE_URL, uuid5

import psycopg
from faker import Faker
from psycopg.rows import dict_row
from pymongo import MongoClient


faker = Faker("pt_BR")
faker.seed_instance(42)


def id_estavel(tipo, valor):
    return uuid5(NAMESPACE_URL, f"acta-mongo:{tipo}:{valor}")


with psycopg.connect(
    os.environ["URL_SEGUNDO_BANCO"],
    options="-c default_transaction_read_only=on",
    row_factory=dict_row,
) as postgres:
    ciclos = postgres.execute(
        """
        SELECT
            ciclo.id,
            ciclo.id_empresa,
            ciclo.id_responsavel,
            ciclo.id_ishikawa_mongo,
            ciclo.criado_em,
            COALESCE(
                string_agg(
                    NULLIF(concat_ws(': ', problema.titulo, problema.descricao), ''),
                    '; ' ORDER BY problema.id
                ),
                ''
            ) AS problema
        FROM pdca.ciclo AS ciclo
        LEFT JOIN pdca.problema AS problema ON problema.id_ciclo = ciclo.id
        GROUP BY ciclo.id
        ORDER BY ciclo.id
        """
    ).fetchall()

    causas_raiz = postgres.execute(
        """
        SELECT id, id_ciclo, id_5_porques_mongo, descricao, criado_em
        FROM pdca.causa_raiz
        ORDER BY id
        """
    ).fetchall()

    participantes = postgres.execute(
        """
        SELECT id_ciclo, id_usuario
        FROM pdca.usuario_ciclo
        ORDER BY id_ciclo, id_usuario
        """
    ).fetchall()

    anexos = postgres.execute(
        """
        SELECT id, id_ciclo
        FROM pdca.anexo
        WHERE categoria = 'RELATORIO' AND status = 'ATIVO'
        ORDER BY id
        """
    ).fetchall()

causas_por_ciclo = {}
for causa in causas_raiz:
    causas_por_ciclo.setdefault(causa["id_ciclo"], []).append(causa)

participantes_por_ciclo = {}
for participante in participantes:
    participantes_por_ciclo.setdefault(participante["id_ciclo"], []).append(
        participante["id_usuario"]
    )

anexos_por_ciclo = {}
for anexo in anexos:
    anexos_por_ciclo.setdefault(anexo["id_ciclo"], anexo["id"])

contagens = {
    "ishikawas": 0,
    "cinco_porques": 0,
    "formularios": 0,
    "licoes_aprendidas": 0,
    "relatorios": 0,
}

categorias_ishikawa = [
    "METODO",
    "MAO_DE_OBRA",
    "MAQUINA",
    "MATERIAL",
    "MEDICAO",
    "MEIO_AMBIENTE",
]

mongo = MongoClient(os.environ["MONGODB_URI"], uuidRepresentation="standard")
database = mongo.get_default_database()

for ciclo in ciclos:
    id_ciclo = ciclo["id"]
    id_ishikawa = ciclo["id_ishikawa_mongo"] or id_estavel("ishikawa", id_ciclo)
    criado_em = ciclo["criado_em"] or datetime.now(timezone.utc)
    causas = causas_por_ciclo.get(id_ciclo, [])

    if not causas:
        causas = [
            {
                "id": f"f{indice}",
                "id_5_porques_mongo": None,
                "descricao": faker.sentence(),
                "criado_em": criado_em,
            }
            for indice in range(1, 4)
        ]

    causas_ishikawa = []
    for causa in causas:
        id_causa = id_estavel("causa-ishikawa", f"{id_ciclo}:{causa['id']}")
        causas_ishikawa.append(
            {
                "id": id_causa,
                "categoria": faker.random_element(categorias_ishikawa),
                "descricao": causa["descricao"] or faker.sentence(),
            }
        )
        documento = {
            "_id": causa["id_5_porques_mongo"]
            or id_estavel("cinco-porques", f"{id_ciclo}:{causa['id']}"),
            "id_ishikawa": id_ishikawa,
            "id_causa_ishikawa": id_causa,
            "id_causa_raiz": causa["id"] if isinstance(causa["id"], int) else None,
            "hipotese": faker.sentence(),
            "porques": [
                {
                    "ordem": ordem,
                    "pergunta": faker.sentence(),
                    "resposta": faker.sentence(),
                }
                for ordem in range(1, 6)
            ],
            "criado_em": causa["criado_em"] or criado_em,
            "atualizado_em": causa["criado_em"] or criado_em,
        }
        database["cinco_porques"].replace_one(
            {"_id": documento["_id"]}, documento, upsert=True
        )
        contagens["cinco_porques"] += 1

    documento = {
        "_id": id_ishikawa,
        "id_empresa": ciclo["id_empresa"],
        "id_ciclo": id_ciclo,
        "problema": ciclo["problema"] or faker.sentence(),
        "causas": causas_ishikawa,
        "criado_em": criado_em,
        "atualizado_em": criado_em,
    }
    database["ishikawas"].replace_one({"_id": id_ishikawa}, documento, upsert=True)
    contagens["ishikawas"] += 1

    id_formulario = id_estavel("formulario", id_ciclo)
    perguntas = [
        {
            "id_pergunta": id_estavel("pergunta", f"{id_ciclo}:1"),
            "texto": faker.sentence(),
            "tipo_resposta": "TEXTO",
            "obrigatoria": True,
            "opcoes": [],
        },
        {
            "id_pergunta": id_estavel("pergunta", f"{id_ciclo}:2"),
            "texto": faker.sentence(),
            "tipo_resposta": "SIM_NAO",
            "obrigatoria": True,
            "opcoes": [],
        },
        {
            "id_pergunta": id_estavel("pergunta", f"{id_ciclo}:3"),
            "texto": faker.sentence(),
            "tipo_resposta": "RADIO",
            "obrigatoria": True,
            "opcoes": ["Baixo", "Médio", "Alto"],
        },
    ]
    usuarios = sorted(
        set(participantes_por_ciclo.get(id_ciclo, []) + [ciclo["id_responsavel"]])
    )
    documento = {
        "_id": id_formulario,
        "id_empresa": ciclo["id_empresa"],
        "id_ciclo": id_ciclo,
        "titulo": faker.sentence(nb_words=5),
        "descricao": faker.paragraph(),
        "tipo": "CHECKLIST",
        "status": "ATIVO",
        "perguntas": perguntas,
        "id_ishikawa": id_ishikawa,
        "ids_usuarios_destinatarios": usuarios,
        "publicado_em": criado_em,
        "criado_em": criado_em,
        "atualizado_em": criado_em,
    }
    database["formularios"].replace_one(
        {"_id": id_formulario}, documento, upsert=True
    )
    contagens["formularios"] += 1

    documento = {
        "_id": id_estavel("licao", id_ciclo),
        "id_usuario": ciclo["id_responsavel"],
        "id_empresa": ciclo["id_empresa"],
        "id_ciclo": id_ciclo,
        "titulo": faker.sentence(nb_words=5),
        "licao": faker.paragraph(),
        "tags": faker.words(nb=3),
        "faseOrigem": faker.random_element(
            ["PLANEJAMENTO", "EXECUCAO", "VERIFICACAO", "PADRONIZACAO"]
        ),
        "resultado": faker.paragraph(),
        "severidade": faker.random_element(["BAIXA", "MEDIA", "ALTA"]),
        "area": faker.job(),
        "criado_em": criado_em,
        "atualizado_em": criado_em,
    }
    database["licoes_aprendidas"].replace_one(
        {"_id": documento["_id"]}, documento, upsert=True
    )
    contagens["licoes_aprendidas"] += 1

    status_relatorio = faker.random_element(["RASCUNHO", "CONCLUIDO", "PUBLICADO"])
    documento = {
        "_id": id_estavel("relatorio", id_ciclo),
        "id_empresa": ciclo["id_empresa"],
        "id_ciclo": id_ciclo,
        "status": status_relatorio,
        "tipo": faker.random_element(
            ["RESUMO_EXECUTIVO", "CICLO_COMPLETO", "AVALIACAO_RESULTADOS", "PADRONIZACAO"]
        ),
        "formato": faker.random_element(["PDF", "PPTX"]),
        "titulo": faker.sentence(nb_words=5),
        "resumo": faker.paragraph(),
        "conteudo": faker.text(max_nb_chars=2000),
        "id_anexo": anexos_por_ciclo.get(id_ciclo),
        "publicado_em": criado_em if status_relatorio == "PUBLICADO" else None,
        "criado_em": criado_em,
        "atualizado_em": criado_em,
    }
    database["relatorios"].replace_one(
        {"_id": documento["_id"]}, documento, upsert=True
    )
    contagens["relatorios"] += 1

mongo.close()

for collection, quantidade in contagens.items():
    print(f"{collection}: {quantidade}")
