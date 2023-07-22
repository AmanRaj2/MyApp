package com.example.myapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class GamesFragment extends Fragment implements View.OnClickListener {

    Button ticTac;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        ticTac = (Button) view.findViewById(R.id.ticTac_btn);
        ticTac.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.btnBackgroundColor), PorterDuff.Mode.MULTIPLY);
        ticTac.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ticTac_btn:
                Intent i = new Intent(getContext(), TicTacToeActivity.class);
                startActivity(i);
                break;
        }
    }
}