package com.multiservicios.valtis.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object ValtisDatabaseProvider {

    private val MIGRATION_2_3 = object : Migration(2, 3) {

        override fun migrate(
            database: SupportSQLiteDatabase
        ) {
            database.execSQL(
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

    @Volatile
    private var INSTANCE: ValtisDatabase? = null

    fun getDatabase(context: Context): ValtisDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ValtisDatabase::class.java,
                "valtis_database"
            )
                .addMigrations(MIGRATION_2_3)
                .build()
                .also {
                    INSTANCE = it
                }
        }
    }
}
