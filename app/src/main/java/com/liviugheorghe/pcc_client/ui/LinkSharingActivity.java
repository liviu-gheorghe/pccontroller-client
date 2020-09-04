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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkSharingActivity extends AppCompatActivity {

    private static final String TAG = LinkSharingActivity.class.getSimpleName();
    private Client client;
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
    private RecyclerAdapter recyclerAdapter;
    private MenuItem actionModeEditLink;
    private MenuItem actionModeShareLink;

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
        RecyclerView linksContainerRecycler = findViewById(R.id.links_recycler_view);
        linksContainerRecycler.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new RecyclerAdapter(this);
        linksContainerRecycler.setAdapter(recyclerAdapter);
        setToolbar();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                serviceBroadcastReceiver,
                new IntentFilter(App.BROADCAST_LEAVE_MAIN_CONTROL_INTERFACE_ACTIVITY)
        );
        sendLinksFloatingActionButton = findViewById(R.id.send_links_fab);
        noLinkSelectedLinearLayout = findViewById(R.id.no_link_selected_linear_layout);
        String extraString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (extraString != null) {
            addViewToLayout(extractLinkFromString(extraString));
        }

        if(savedInstanceState != null) {
            ArrayList<String> links = savedInstanceState.getStringArrayList("LINKS");
            if(links != null) {
                for (String link : links) {
                    recyclerAdapter.addLink(link);
                }
            }
        }

        bindClientService();
        sendLinksFloatingActionButton.setOnClickListener(view -> sendLinks());
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

    private void toggleActionModeMenuItemVisibility(MenuItem item,boolean visible) {
        if(item != null) {
            item.setVisible(visible);
            item.setEnabled(visible);
        }
    }

    public ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.link_sharing_contextual_action_bar,menu);
            try {
                actionModeEditLink = menu.findItem(R.id.link_sharing_action_edit);
                MenuItem actionModeDeleteLink = menu.findItem(R.id.link_sharing_action_delete);
                actionModeShareLink = menu.findItem(R.id.link_sharing_action_share);
            } catch(Exception e) {
                e.printStackTrace();
            }
            recyclerAdapter.updateActionInformation();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch(item.getItemId()) {
                case R.id.link_sharing_action_edit:
                    ArrayList<Integer> indices = recyclerAdapter.getSelectedLinksIndicesArray();
                    int position = indices.get(0);
                    showEditLinkDialog(recyclerAdapter.getLinks().get(position),position);
                    break;
                case R.id.link_sharing_action_delete:
                    recyclerAdapter.removeSelectedLinks();
                    actionMode.finish();
                    break;
                case R.id.link_sharing_action_share:
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_TEXT,recyclerAdapter.getSelectedLinks().get(0));
                    i.setType("text/plain");
                    startActivity(Intent.createChooser(i, "Send link"));
                    actionMode.finish();
                    break;
                default:
                    return false;
            }
            updateConnectionInfo();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            recyclerAdapter.notifyDataSetChanged();
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
            if(recyclerAdapter.getLinks().size() > 0) {
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

    private void showEditLinkDialog(String oldLink,int linkPosition) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText input;
        input = createEditLinkTextInput(oldLink);
        builder.setTitle(R.string.edit_link_dialog_title)
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String text = input.getText().toString();
                    recyclerAdapter.updateLinkAtPosition(linkPosition,text);
                    actionMode.finish();
                })
                .setNegativeButton(R.string.add_link_dialog_cancel, (dialog, which) -> {
                    actionMode.finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void addViewToLayout(String link) {
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("LINKS",recyclerAdapter.getLinks());
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

        public List<String> getSelectedLinks() {
            List<String> l = new ArrayList<>();
            for(int i = 0;i<links.size();i++) {
                if(selectedLinks.get(i,false)) {
                    l.add(links.get(i));
                }
            }
            return l;
        }

        public void updateLinkAtPosition(int position,String newLink) {
            if(position < 0 || position >= links.size()) return;
            links.set(position,newLink);
            notifyDataSetChanged();
        }


        public ArrayList<Integer> getSelectedLinksIndicesArray() {
            ArrayList<Integer> l = new ArrayList<>();
            for(int i = 0;i<links.size();i++) {
                if(selectedLinks.get(i,false)) {
                    l.add(i);
                }
            }
            return l;
        }


        public void removeSelectedLinks() {
            for(int i = links.size() -1;i >= 0;i--) {
                if(selectedLinks.get(i,false)) {
                    selectedLinks.delete(i);
                    links.remove(i);
                    notifyDataSetChanged();
                }
            }
        }


        public void updateActionInformation() {
            if(getSelectedLinksCount() == 0) {
                exitActionModeIfNoElementIsSelected();
            }
            else if(actionMode != null) {
                actionMode.setTitle(
                        String.format(
                                "%s %s",
                                getSelectedLinksCount(),
                                (
                                        getSelectedLinksCount() == 1 ?
                                                getString(R.string.link_sharing_action_mode_title_plural) :
                                                getString(R.string.link_sharing_action_mode_title)
                                )
                         )
                );
                    toggleActionModeOptionsIncompatibleWithMultiSelection();
            }
        }

        private void toggleActionModeOptionsIncompatibleWithMultiSelection() {
            boolean visible = (getSelectedLinksCount() == 1);
                toggleActionModeMenuItemVisibility(actionModeShareLink, visible);
                toggleActionModeMenuItemVisibility(actionModeEditLink, visible);
        }

        private void exitActionModeIfNoElementIsSelected() {
            if(getSelectedLinksCount() == 0 && actionMode != null) {
                actionMode.finish();
            }
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
            return new RecyclerAdapter.LinkViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            RecyclerAdapter.LinkViewHolder linkViewHolder = (RecyclerAdapter.LinkViewHolder) holder;
            linkViewHolder.getLinkView().setText(links.get(position));
            linkViewHolder.getLinkView().setActivated(selectedLinks.get(position,false) && actionMode != null);
            linkViewHolder.getLinkView().setOnLongClickListener(
                    v -> {
                        if(actionMode != null) return true;
                        actionMode = LinkSharingActivity.this.startActionMode(actionModeCallback);
                        v.setActivated(true);
                        markLinkAsSelected(position);
                        updateActionInformation();
                        return true;
                    }
            );

            linkViewHolder.getLinkView().setOnClickListener(
                v -> {
                    if(actionMode != null) {
                        if(!selectedLinks.get(position, false)) {
                            v.setActivated(true);
                            markLinkAsSelected(position);
                        }
                        else {
                            v.setActivated(false);
                            selectedLinks.delete(position);
                        }
                        updateActionInformation();
                    }
                }
            );
        }

        private void markLinkAsSelected(int position) {
            selectedLinks.put(position, true);
            notifyDataSetChanged();
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
            }
        }
    }
}