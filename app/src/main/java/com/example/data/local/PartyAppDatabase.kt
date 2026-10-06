package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    CustomCategoryEntity::class,
    CustomQuestionEntity::class,
    DeletedItemEntity::class,
    CustomLoopWordEntity::class,
    CustomSpyLocationEntity::class,
    CustomCharadesWordEntity::class
  ],
  version = 3,
  exportSchema = false
)

abstract class PartyAppDatabase : RoomDatabase() {
  abstract fun customPartyDao(): CustomPartyDao

  companion object {
    @Volatile
    private var INSTANCE: PartyAppDatabase? = null

    fun getDatabase(context: Context): PartyAppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PartyAppDatabase::class.java,
          "party_game_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
