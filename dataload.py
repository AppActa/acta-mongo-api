import os

import psycopg
from psycopg.rows import dict_row
from pymongo import MongoClient


with psycopg.connect(
    os.environ["URL_SEGUNDO_BANCO"],
    options="-c default_transaction_read_only=on",
    row_factory=dict_row,
) as postgres:
    ciclos = postgres.execute(
        """
        SELECT
            ciclo.id_ishikawa_mongo AS id,
            ciclo.id_empresa,
            ciclo.id AS id_ciclo,
            COALESCE(
                string_agg(
                    NULLIF(concat_ws(': ', problema.titulo, problema.descricao), ''),
                    '; ' ORDER BY problema.id
                ),
                'Problema do ciclo não informado'
            ) AS problema
        FROM pdca.ciclo AS ciclo
        LEFT JOIN pdca.problema AS problema ON problema.id_ciclo = ciclo.id
        WHERE ciclo.id_ishikawa_mongo IS NOT NULL
        GROUP BY ciclo.id, ciclo.id_empresa, ciclo.id_ishikawa_mongo
        ORDER BY ciclo.id
        """
    ).fetchall()

with MongoClient(os.environ["MONGODB_URI"], uuidRepresentation="standard") as mongo:
    database = mongo.get_default_database()

    for ciclo in ciclos:
        database["ishikawas"].update_one(
            {"_id": ciclo["id"]},
            {
                "$setOnInsert": {
                    "_id": ciclo["id"],
                    "id_empresa": ciclo["id_empresa"],
                    "id_ciclo": ciclo["id_ciclo"],
                    "problema": ciclo["problema"],
                    "causas": [],
                }
            },
            upsert=True,
        )

print(f"Ishikawa carregados: {len(ciclos)}")
