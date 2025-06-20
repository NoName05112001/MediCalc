package com.example.medicalc

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Clase para manejar las operaciones de autenticación con Firebase Authentication
 * y la gestión básica de perfiles de usuario en Firestore.
 */
class AuthManager {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore // <-- Añadimos la instancia de Firestore
    private val usersCollection = db.collection("users") // <-- Referencia a la colección 'users' en Firestore

    /**
     * Registra un nuevo usuario con email, contraseña y nombre.
     * Después del registro en Firebase Auth, guarda el nombre, email y UID en Firestore.
     * @return true si el registro y el guardado en Firestore fueron exitosos, false en caso contrario.
     */
    suspend fun signUp(name: String, email: String, password: String): Boolean {
        return try {
            // 1. Registrar usuario con Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                // 2. Si el registro en Auth fue exitoso, guardar perfil en Firestore
                val userProfile = UserProfile(name = name, email = email)
                // Usamos el UID de Firebase Auth como ID del documento en Firestore
                usersCollection.document(user.uid).set(userProfile).await()
                true
            } else {
                // Esto no debería pasar si await() no lanza una excepción, pero es una buena práctica.
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Inicia sesión con email y contraseña.
     * @return true si el inicio de sesión fue exitoso, false en caso contrario.
     */
    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Obtiene el ID del usuario actualmente autenticado.
     * @return El UID del usuario si está autenticado, o null si no hay sesión.
     */
    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Verifica si hay un usuario actualmente autenticado.
     * @return true si hay un usuario logueado, false en caso contrario.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Obtiene el perfil de usuario desde Firestore.
     * @param userId El UID del usuario cuyo perfil se desea obtener.
     * @return El objeto UserProfile si se encuentra, o null si no.
     */
    suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val document = usersCollection.document(userId).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}