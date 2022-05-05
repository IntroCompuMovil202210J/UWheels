package com.abmodel.uwheels.ui.shared.signup

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abmodel.uwheels.R
import com.abmodel.uwheels.ui.shared.login.FormResult
import com.abmodel.uwheels.util.isEmailValid
import com.abmodel.uwheels.util.isPasswordValid
import com.google.firebase.auth.FirebaseAuth

class SignUpViewModel @JvmOverloads constructor(
	application: Application
) : AndroidViewModel(application) {

	private val _signUpResult = MutableLiveData<FormResult>()
	val signUpResult: LiveData<FormResult> = _signUpResult

	private val mAuth = FirebaseAuth.getInstance()

	fun signUp(
		name: String, lastName: String, phone: String,
		email: String, password: String, passwordAgain: String
	) {

		if (checkSignUpForm(
				name, lastName, phone,
				email, password, passwordAgain
			)
		) {

			mAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener { task ->
					if (task.isSuccessful) {
						Log.d(SignUpFragment.TAG, "Sign up successful")
						_signUpResult.value = FormResult(
							success = true, error = null
						)
					} else {
						Log.d(SignUpFragment.TAG, "Sign up failed")
						_signUpResult.value = FormResult(
							error = R.string.signup_failed
						)
					}
				}
		}
	}

	private fun checkSignUpForm(
		name: String, lastName: String, phone: String,
		email: String, password: String, passwordAgain: String
	): Boolean {

		return if (name.isEmpty() || lastName.isEmpty() || phone.isEmpty() ||
			email.isEmpty() || password.isEmpty() || passwordAgain.isEmpty()
		) {
			_signUpResult.value = FormResult(error = R.string.login_failed_empty_fields)
			false
		} else if (!isEmailValid(email)) {
			_signUpResult.value = FormResult(error = R.string.invalid_email)
			false
		} else if (!isPasswordValid(password)) {
			_signUpResult.value = FormResult(error = R.string.invalid_password)
			false
		} else if (password != passwordAgain) {
			_signUpResult.value = FormResult(error = R.string.password_mismatch)
			false
		} else {
			true
		}
	}
}
