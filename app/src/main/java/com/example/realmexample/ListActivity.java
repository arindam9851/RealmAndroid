package com.example.realmexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.realmexample.adapter.RecyclerAdapter;
import com.example.realmexample.utils.AppUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmResults;

public class ListActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView mRecycler;
    private RecyclerAdapter mAdapter;
    private Realm mRealm;
    private RealmResults<MyBook> data;
    private FloatingActionButton mFabAdd;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mRealm=Realm.getDefaultInstance();
        initview();
        fetchdatafromDB();
    }

    private void fetchdatafromDB() {
        data=mRealm.where(MyBook.class).findAll();
        setAdapter(data);
    }

    private void setAdapter(RealmResults<MyBook> data) {
        mAdapter=new  RecyclerAdapter(this,data);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
    }

    private void initview() {
        mRecycler=(RecyclerView)findViewById(R.id.recycler);
        mFabAdd=(FloatingActionButton)findViewById(R.id.fab_add);
        mFabAdd.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    public void showPopUp(final int pos, View view, final MyBook myBook) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater()
                .inflate(R.menu.item_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);
                        alertDialogBuilder.setMessage(getResources().getString(R.string.do_you_want_to_delete));
                        alertDialogBuilder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        deleteItemFromDB(pos);
                                    }
                                });

                        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                        break;
                    case R.id.menu_edit:
                            openEdit(myBook);

                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    private void openEdit(final MyBook myBook) {
        dialog = new Dialog(this);
        AppUtils.getInstance().openAnimatedDialog(dialog, this, (byte) 1);
        final AppCompatEditText etTitle=(AppCompatEditText)dialog.findViewById(R.id.et_tittle);
        final AppCompatEditText etDesc=(AppCompatEditText)dialog.findViewById(R.id.et_desc);
        etTitle.setText(myBook.getTitle());
        etDesc.setText(myBook.getDesc());
        AppCompatButton btnSumbit=(AppCompatButton)dialog.findViewById(R.id.btn_submit) ;
        btnSumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyBook obj = new MyBook();
                obj.setTitle(etTitle.getText().toString());
                obj.setDesc(etDesc.getText().toString());
                obj.setId(myBook.getId());
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // This will create a new object in Realm or throw an exception if the
                        // object already exists (same primary key)
                        // realm.copyToRealm(obj);

                        // This will update an existing object with the same primary key
                        // or create a new object if an object with no primary key = 42
                        realm.copyToRealmOrUpdate(obj);
                        dialog.dismiss();
                        Toast.makeText(ListActivity.this,"Record update Succesfully",Toast.LENGTH_LONG).show();
                        fetchdatafromDB();
                    }
                });
            }
        });

        dialog.show();
    }

    private void deleteItemFromDB(final int pos) {
        //final RealmResults<MyBook> results = mRealm.where(MyBook.class).findAll();

        // All changes to data must happen in a transaction
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // remove single match
                //results.deleteFirstFromRealm();
                //results.deleteLastFromRealm();

                // remove a single object
                MyBook dog = data.get(pos);
                dog.deleteFromRealm();
                fetchdatafromDB();

                // Delete all matches
                // results.deleteAllFromRealm();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_add:
                dialog = new Dialog(this);
                AppUtils.getInstance().openAnimatedDialog(dialog, this, (byte) 1);
                final AppCompatEditText etTitle=(AppCompatEditText)dialog.findViewById(R.id.et_tittle);
                final AppCompatEditText etDesc=(AppCompatEditText)dialog.findViewById(R.id.et_desc);
                AppCompatButton btnSumbit=(AppCompatButton)dialog.findViewById(R.id.btn_submit) ;

                Number num = mRealm.where(MyBook.class).max("id");
                final int nextID;
                if(num == null) {
                    nextID = 1;
                } else {
                    nextID = num.intValue() + 1;
                }
                btnSumbit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRealm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm bgRealm) {

                                MyBook user = bgRealm.createObject(MyBook.class,nextID);
                                user.setTitle(etTitle.getText().toString().trim());
                                user.setDesc(etDesc.getText().toString().trim());
                                //user.setId(nextID);
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                // Transaction was a success.
                                Log.d("status","Success");
                                dialog.dismiss();
                                fetchdatafromDB();
                                Toast.makeText(ListActivity.this,"Record insert Succesfully",Toast.LENGTH_LONG).show();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                // Transaction failed and was automatically canceled.
                                Log.d("status","failed");
                            }
                        });
                    }
                });

                dialog.show();
                break;
        }
    }
}
