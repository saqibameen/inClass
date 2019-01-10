package com.example.saqibameen.app01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Declare instance of FirebaseAuth.
    private FirebaseAuth mAuth;
    private Button loginButton;
//    private final int  = R.layout.activity_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the firebase auth on creation of app.
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("inClass");

    }

    public void login(View view) {


        String email = ((TextView)findViewById(R.id.txtEmail)).getText().toString();
        String password = ((TextView)findViewById(R.id.txtPassword)).getText().toString();

        if(email.isEmpty() || password.isEmpty()) {
            return;
        }

        Toast.makeText(MainActivity.this, "Loading...",
                Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(MainActivity.this, "I'm in", Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {

                            // Show Logged in Msg.
                            Log.d("Logged In", "signInWithEmail:success");
                            Toast.makeText(MainActivity.this, "Authenticated.",
                                    Toast.LENGTH_SHORT).show();

                            // Get current user.
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Forward to next activity.
                            Intent intent = new Intent(MainActivity.this, Courses.class);
                            // Attach the user data.
                            intent.putExtra("user", user.getUid());
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("NOT LOGGED IN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }
                    }
                });
    }
}
