package com.multiservicios.valtis.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object ValtisDatabaseProvider {

    private val MIGRATION_2_3 = object : Migration(2, 3) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS gastos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    monto REAL NOT NULL,
                    concepto TEXT NOT NULL,
                    fecha INTEGER NOT NULL,
                    categoria TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {

        override fun migrate(
            db: SupportSQLiteDatabase
        ) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS deudas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nombre TEXT NOT NULL,
                    montoPago REAL NOT NULL,
                    pagosRestantes INTEGER NOT NULL,
                    periodicidad TEXT NOT NULL,
                    diaPago INTEGER NOT NULL,
                    fechaProximoPago INTEGER NOT NULL,
                    apartado REAL NOT NULL,
                    faltanteAnterior REAL NOT NULL,
                    activa INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    @Volatile
    private var INSTANCE: ValtisDatabase? = null

    fun getDatabase(context: Context): ValtisDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ValtisDatabase::class.java,
                "valtis_database"
            )
                .addMigrations(
                    MIGRATION_2_3,
                    MIGRATION_3_4
                )
                .build()
                .also {
                    INSTANCE = it
                }
        }
    }
}
