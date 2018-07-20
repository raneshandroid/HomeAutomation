package com.raneshprasad.smartbudget;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText username;
    EditText password;
    Button createAccount;
    EditText passwordRepeat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.editTextUsernameCreate);
        password = findViewById(R.id.editTextPasswordCreate);
        passwordRepeat = findViewById(R.id.editTextPasswordRepeat);

        createAccount = findViewById(R.id.buttonCreateAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().toString().equals(passwordRepeat.getText().toString())) {
                    mAuth.createUserWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(CreateAccount.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Firebase Auth Status", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent i = new Intent(CreateAccount.this, Data.class);
                                        startActivity(i);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Firebase Auth Status", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(CreateAccount.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }
            }
        });





    }
}
