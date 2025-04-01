package com.example.projecthub.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class authViewModel(application: Application): AndroidViewModel(application) {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("UserSession", Context.MODE_PRIVATE)


    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        val user  = auth.currentUser

        val isRemembered = sharedPreferences.getBoolean("RememberMe", false)

        if(user == null || !user.isEmailVerified){
            _authState.value = AuthState.Unauthenticated
        }else{
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email : String , password : String){

        if (email.isEmpty()||password.isEmpty()){
            _authState.value = AuthState.Error("email or password missing")
            return
        }


        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        _authState.value = AuthState.Authenticated
                    }else{
                        _authState.value = AuthState.Error("Please verify your email before login")
                        auth.signOut()
                    }
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")

                }
            }
    }

    fun signup(email : String , password : String){

        if (email.isEmpty()||password.isEmpty()){
            _authState.value = AuthState.Error("email or password missing")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if (task.isSuccessful){
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener{verifyTask->
                            if(verifyTask.isSuccessful){
                                _authState.value = AuthState.Error("Verification email sent !Please verify your email.")
                                auth.signOut()
                            }else{
                                _authState.value = AuthState.Error("failed to send email verification")
                            }
                        }
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

    fun resendVerificationEmail(){
        val user =  auth.currentUser
        if(user != null && !user.isEmailVerified){
            user.sendEmailVerification()
                .addOnCompleteListener{task->
                    if(task.isSuccessful){
                        _authState.value = AuthState.Error("Verification email resent. Please check your email.")
                    }else{
                        _authState.value = AuthState.Error("Failed to resend email verification")
                    }
                }
        }else{
            _authState.value = AuthState.Error("User not found or already verified")
        }
    }

    fun signout(){
        auth.signOut()
        clearLoginSession()
        _authState.value = AuthState.Unauthenticated
    }

    private fun saveLoginSession(rememberMe: Boolean) {
        sharedPreferences.edit().putBoolean("RememberMe", rememberMe).apply()
    }

    private fun clearLoginSession() {
        sharedPreferences.edit().remove("RememberMe").apply()
    }
}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message :String) : AuthState()
}