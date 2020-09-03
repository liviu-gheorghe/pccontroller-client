package com.liviugheorghe.pcc_client.ui;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.liviugheorghe.pcc_client.App;
import com.liviugheorghe.pcc_client.R;
import com.liviugheorghe.pcc_client.backend.Client;
import com.liviugheorghe.pcc_client.backend.DispatchedActionsCodes;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkSharingActivity extends AppCompatActivity {


    private static final String TAG = LinkSharingActivity.class.getSimpleName();
    private Client client;
    private boolean isLinkAdded = true;
    private LinearLayout noLinkSelectedLinearLayout;
    private Pattern urlPattern = Pattern.compile("https?://.+");
    private ActionMode actionMode;
    private FloatingActionButton sendLinksFloatingActionButton;
    private final BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            if (intent.getAction().equals(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY)) {
                onResume();
            }
        }
    };

    private ServiceConnection serviceConnection;
/*    private View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(actionMode != null) return false;
            actionMode = LinkSharingActivity.this.startActionMode(actionModeCallback);
            v.setSelected(true);
            return true;
        }
    };*/
    private RecyclerView linksContainerRecycler;
    private RecyclerAdapter recyclerAdapter;

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_sharing);
        linksContainerRecycler = findViewById(R.id.links_recycler_view);
        linksContainerRecycler.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapter(this);
        linksContainerRecycler.setAdapter(recyclerAdapter);



        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://gitlab.com");
        recyclerAdapter.addLink("https://facebook.com");
        recyclerAdapter.addLink("https://yahoo.com");
        recyclerAdapter.addLink("https://google.com");
        recyclerAdapter.addLink("https://nasa.gov");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");
        recyclerAdapter.addLink("https://github.com");


        setToolbar();
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceBroadcastReceiver, new IntentFilter(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY));
        sendLinksFloatingActionButton = findViewById(R.id.send_links_fab);
        noLinkSelectedLinearLayout = findViewById(R.id.no_link_selected_linear_layout);
        String extraString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (extraString != null) {
            addViewToLayout(extractLinkFromString(extraString));
        } else isLinkAdded = false;

        bindClientService();
        sendLinksFloatingActionButton.setOnClickListener(view -> sendLinks());
        updateConnectionInfo();
    }

    private boolean checkIfStringIsValidUrl(String s) {
        if(s == null) return false;
        Matcher matcher = urlPattern.matcher(s);
        return matcher.find();
    }

    private String extractLinkFromString(String s) {
        if(!checkIfStringIsValidUrl(s)) return null;
        Matcher matcher = urlPattern.matcher(s);
        if(matcher.find())
        return matcher.group(0);
        else return null;
    }

    private void addLink() {
        showAddLinkDialog();
    }

    private void sendLinks() {
        for(String link : recyclerAdapter.getLinks()) {
            client.getConnection().dispatchAction(DispatchedActionsCodes.ACTION_OPEN_LINK_IN_BROWSER, link);
        }
        finish();
    }

    public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.link_sharing_contextual_action_bar,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Log.d("Link", "onActionItemClicked: " + item.getItemId());
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {


            Log.d(TAG, "onDestroyActionMode: Number of selected links ----> " + recyclerAdapter.getSelectedLinksCount());
            actionMode = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_link_sharing_appbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_add_link) {
            addLink();
            return true;
        }
        return false;
    }

    private void bindClientService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                Client.ClientBinder binder = (Client.ClientBinder) service;
                client = binder.getClient();
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
            }
        };
        Intent serviceIntent = new Intent(LinkSharingActivity.this, Client.class);
        bindService(serviceIntent, serviceConnection, 0);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateConnectionInfo();
    }

    private void updateConnectionInfo() {

        if (App.CONNECTION_ALIVE) {
            if(isLinkAdded) {
                sendLinksFloatingActionButton.setVisibility(View.VISIBLE);
                noLinkSelectedLinearLayout.setVisibility(View.INVISIBLE);
                noLinkSelectedLinearLayout.setScaleY(0);
            }
            else {
                sendLinksFloatingActionButton.setVisibility(View.INVISIBLE);
                noLinkSelectedLinearLayout.setVisibility(View.VISIBLE);
                noLinkSelectedLinearLayout.setScaleY(1);
            }
        }
        else {
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    private void showAddLinkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText input;
        input = createEditLinkTextInput(null);
        builder.setTitle(R.string.add_link_dialog_title)
                .setView(input)
                .setPositiveButton("OK",(dialog,which) -> {
                    String enteredString = input.getText().toString();
                    if(!checkIfStringIsValidUrl(enteredString))
                        Toast.makeText(this, R.string.invalid_link_toast_text,Toast.LENGTH_SHORT).show();
                    else
                    addViewToLayout(enteredString.trim());
                })
                .setNegativeButton(R.string.add_link_dialog_cancel,(dialog, which) -> {
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addViewToLayout(String link) {
        isLinkAdded = true;
        recyclerAdapter.addLink(link);
        updateConnectionInfo();
    }

    private EditText createEditLinkTextInput(String text) {
        EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        input.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);
        input.setSingleLine();
        if(text != null)
        input.setText(text);
        input.setHint(R.string.add_link_dialog_input_hint);
        return input;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceBroadcastReceiver);
        if (serviceConnection != null)
            unbindService(serviceConnection);
    }

    class RecyclerAdapter extends RecyclerView.Adapter {


        private LayoutInflater layoutInflater;
        private Context context;
        private ArrayList<String> links = new ArrayList<>();
        private SparseBooleanArray selectedLinks;

        public RecyclerAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(this.context);
            selectedLinks = new SparseBooleanArray();
        }


        public int getSelectedLinksCount() {
            return selectedLinks.size();
        }


        public ArrayList<String> getLinks() {
            return links;
        }

        public void addLink(String link) {
            links.add(link);
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View viewItem = layoutInflater.inflate(R.layout.link_viewholder_item,parent,false);

            viewItem.setOnLongClickListener(
                    v -> {
                        if(actionMode != null) return true;
                        actionMode = LinkSharingActivity.this.startActionMode(actionModeCallback);
//                        v.setActivated(true);
                        return true;
                    }
            );
            return new RecyclerAdapter.LinkViewHolder(viewItem);
        }


        private void onViewHolderItemClicked(View v,int position) {
            if(actionMode == null) {
                // TODO
            }
            else {
                if(!selectedLinks.get(position, false)) {
                    v.setActivated(true);
                    selectedLinks.put(position,true);
                }
                else {
                    v.setActivated(false);
                    selectedLinks.delete(position);
                }
            }
        }


        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            RecyclerAdapter.LinkViewHolder linkViewHolder = (RecyclerAdapter.LinkViewHolder) holder;
            linkViewHolder.getLinkView().setText(links.get(position));
            linkViewHolder.getLinkView().setActivated(selectedLinks.get(position,false));
            onViewHolderItemClicked(linkViewHolder.getLinkView(),position);

        }

        @Override
        public int getItemCount() {
            return links.size();
        }

        private final class LinkViewHolder extends RecyclerView.ViewHolder {

            private TextView linkView;

            public TextView getLinkView() {
                return linkView;
            }

            public LinkViewHolder(@NonNull View itemView) {
                super(itemView);
                linkView = itemView.findViewById(R.id.link_viewholder_item);
/*                linkView.setOnLongClickListener(v -> {
                    if(actionMode != null) return false;
                    actionMode = LinkSharingActivity.this.startActionMode(actionModeCallback);
                    v.setSelected(true);
                    return true;
                });*/
            }
        }
    }
}
