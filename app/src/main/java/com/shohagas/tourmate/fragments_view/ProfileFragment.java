package com.shohagas.tourmate.fragments_view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shohagas.tourmate.LoginActivity;
import com.shohagas.tourmate.R;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private Context context;
    private ImageView profilePicIv;
    private TextView userNameTv, emailTv, emailValidationTv;
    private Button signOutBt;

    private FirebaseAuth mFirebaseAuth;
    private  FirebaseUser user;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePicIv = view.findViewById(R.id.image_view_profilePic);
        userNameTv = view.findViewById(R.id.textView_userName);
        emailTv = view.findViewById(R.id.textView_email);
        emailValidationTv = view.findViewById(R.id.textView_email_validation);
        signOutBt = view.findViewById(R.id.buttonSingOut);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            if(photoUrl !=  null){
                Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(profilePicIv);
            }
            userNameTv.setText(name);
            emailTv.setText(email);
            if(emailVerified){
                emailValidationTv.setText("Email is Verified");
            }else{
                emailValidationTv.setText("Email is not Verified");
            }
        } else {
            // No user is signed in
            startActivity(new Intent(context, LoginActivity.class));
        }
        profilePicIv.setOnClickListener(imageOnClickListener);
        signOutBt.setOnClickListener(signoutOnClickListener);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private  View.OnClickListener imageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener signoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            //User Sign out Code
            mFirebaseAuth.signOut();
            getActivity().finish();
            startActivity(new Intent(context, LoginActivity.class));
        }
    };

}
