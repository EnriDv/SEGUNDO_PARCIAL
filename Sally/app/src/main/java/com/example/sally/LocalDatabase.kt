package com.example.sally

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val salonName: String,
    val salonAddress: String, // Revisa que tengas estos campos nuevos
    val date: Long,
    val time: String,
    val specialistName: String,
    val serviceName: String,
    val price: String,
    val status: String
)

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert
    suspend fun insertAppointment(appointment: Appointment)

    @Query("UPDATE appointments SET status = 'Cancelled' WHERE id = :id")
    suspend fun cancelAppointment(id: Int)
}

@Database(entities = [Appointment::class], version = 2) // Versión 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sally_database"
                )
                    // ESTA LÍNEA ES IMPORTANTE: Permite borrar la BD si la versión cambia
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}