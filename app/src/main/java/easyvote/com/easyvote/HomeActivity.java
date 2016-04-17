package easyvote.com.easyvote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by vishwesh on 17/4/16.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import easyvote.com.SessionManager;
import easyvote.com.adapter.ElectionsAdapter;
import easyvote.com.model.Election;

public class HomeActivity extends AppCompatActivity {

    RecyclerView rv;
    ElectionsAdapter adapter;
    SessionManager session;
    ProgressBar uploadProgressBar;
    String id;
    ArrayList<Election> electionsList;
    CoordinatorLayout coordinatorLayout1;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressDialog = new ProgressDialog(this);


        session = new SessionManager(getApplicationContext());

        electionsList = new ArrayList<Election>();

        rv = (RecyclerView) findViewById(R.id.main_recycler);

        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        rv.setLayoutManager(llm);

        adapter = new ElectionsAdapter(HomeActivity.this.getApplicationContext());
        adapter.updateList(electionsList);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        String n = electionsList.get(position).id;
                        Intent i = new Intent(HomeActivity.this, VotingPreference.class);
                        i.putExtra("u", n);
                        startActivity(i);
                    }
                })
        );

        progressDialog.setMessage("Loading elections list..");
        progressDialog.show();
        Ion.with(getApplicationContext())
                .load("GET", Config.mainURL + "/getAllElections.php")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        try {

                            JsonArray jsonArray = result.get("electionname").getAsJsonArray();

                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                                String id = jsonObject.get("id").getAsString();
                                String name = jsonObject.get("name").getAsString();
                                String place = jsonObject.get("place").getAsString();
                                String date = jsonObject.get("date").getAsString();
                                String from_time = jsonObject.get("from_time").getAsString();
                                String positionId = jsonObject.get("positionId").getAsString();
                                String organiserId = jsonObject.get("organiserId").getAsString();

                                electionsList.add(new Election(id, name, place, date, from_time, positionId, organiserId));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        catch (Exception et)
                        {
                            Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_SHORT).show();
                        }
                    }

                });

    }

}