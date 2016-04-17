package easyvote.com.easyvote;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.StreamBody;
import com.koushikdutta.ion.Ion;

import easyvote.com.easyvote.Config;
import easyvote.com.easyvote.R;
import easyvote.com.model.Election;

public class VotingPreference extends AppCompatActivity {

    Spinner Ipref, IIpref, IIIpref, GsecSports, GsecCult, GsecTech;
    Button button;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voting_pref);
        Ipref = (Spinner) findViewById(R.id.spinner_1stpref);
        IIpref = (Spinner) findViewById(R.id.spinner_2ndpref);
        IIIpref = (Spinner) findViewById(R.id.spinner_3rdpref);
        GsecCult = (Spinner) findViewById(R.id.spinner_gsecCult);
        GsecSports = (Spinner) findViewById(R.id.spinner_gsecSports);
        GsecTech = (Spinner) findViewById(R.id.spinner_gsecTech);
        button = (Button) findViewById(R.id.button);
        progressDialog = new ProgressDialog(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.candidate_names)); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Ipref.setAdapter(spinnerArrayAdapter);
        IIIpref.setAdapter(spinnerArrayAdapter);
        IIpref.setAdapter(spinnerArrayAdapter);
        GsecTech.setAdapter(spinnerArrayAdapter);
        GsecSports.setAdapter(spinnerArrayAdapter);
        GsecCult.setAdapter(spinnerArrayAdapter);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Your vote is under process.Please wait...");
                progressDialog.show();
                final String s = "can"+Ipref.getSelectedItemPosition()+1;
                Ion.with(getApplicationContext())
                        .load("POST", Config.mainURL + "/getAllVotes.php")
                        .setBodyParameter("cId", s)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                progressDialog.dismiss();

                                try {

                                    JsonArray jsonArray = result.get("vote").getAsJsonArray();


                                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                                    String vote = jsonObject.get("vote").getAsString();


                                    Ion.with(getApplicationContext())
                                            .load("POST", Config.mainURL + "/voteCandidate.php")
                                            .setBodyParameter("cId", String.valueOf(s))
                                            .setBodyParameter("vote", vote + 1)
                                            .setBodyParameter("getvote", "")

                                            .asJsonObject()

                                            .setCallback(new FutureCallback<JsonObject>() {
                                                @Override
                                                public void onCompleted(Exception e, JsonObject result) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Vote Successfully Cast", Toast.LENGTH_SHORT).show();

                                                }

                                            });

                                } catch (Exception et) {
                                    et.printStackTrace();
                                    progressDialog.dismiss();

                                    Toast.makeText(getApplicationContext(), "Vote Successfully Cast", Toast.LENGTH_SHORT).show();


                                }
                            }
                        });


            }
        });

    }
}