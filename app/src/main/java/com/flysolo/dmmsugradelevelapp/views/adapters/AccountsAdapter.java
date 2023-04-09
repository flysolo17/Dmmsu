package com.flysolo.dmmsugradelevelapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flysolo.dmmsugradelevelapp.R;
import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.Classroom;
import com.flysolo.dmmsugradelevelapp.model.Content;
import com.flysolo.dmmsugradelevelapp.views.viewholders.AccountsViewHolder;
import com.flysolo.dmmsugradelevelapp.views.viewholders.Head;

import java.util.ArrayList;

public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int STUDENT = 1;
    int HEADER = 0;
    Context context;
    ArrayList<Accounts> accounts;
    Classroom classroom;
    AccountClickListener accountClickListener;
    public interface AccountClickListener {
        void addStudent(int position);
        void removeStudent(int position);
    }
    public AccountsAdapter(Context context, ArrayList<Accounts> accounts, Classroom classroom,AccountClickListener accountClickListener) {
        this.context = context;
        this.accounts = accounts;
        this.classroom = classroom;
        this.accountClickListener = accountClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_account_head,parent,false);
            return new Head(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.row_accounts,parent,false);
            return new AccountsViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == HEADER) {
            Head head = (Head) holder;
            if (position == 0) {
                head.textType.setText("CLASS STUDENTS");
                head.textCount.setText(String.valueOf(getStudent().size()));
            } else {
                head.textType.setText("COMPETITORS");
                head.textCount.setText(String.valueOf(getNonStudent().size()));
            }
        } else {
            AccountsViewHolder accountsViewHolder = (AccountsViewHolder) holder;
            Accounts account = accounts.get(position);
            accountsViewHolder.textFullname.setText(account.getName());
            if (classroom.getStudents().contains(account.id)) {
                accountsViewHolder.buttonAdd.setVisibility(View.GONE);
                accountsViewHolder.buttonRemove.setVisibility(View.VISIBLE);
            } else {
                accountsViewHolder.buttonAdd.setVisibility(View.VISIBLE);
                accountsViewHolder.buttonRemove.setVisibility(View.GONE);
            }
            accountsViewHolder.buttonAdd.setOnClickListener(view -> accountClickListener.addStudent(position));
            accountsViewHolder.buttonRemove.setOnClickListener(view -> accountClickListener.removeStudent(position));
        }
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (accounts.get(position) == null)
            return HEADER;
        return STUDENT;

    }
    private ArrayList<Accounts> getStudent() {
        ArrayList<Accounts> arrayList = new ArrayList<>();
        for (Accounts accounts: accounts) {
            if (accounts!= null) {
                if (classroom.getStudents().contains(accounts.getId())) {
                    arrayList.add(accounts);
                }
            }
        }
        return arrayList;
    }
    private ArrayList<Accounts> getNonStudent(){
        ArrayList<Accounts> arrayList = new ArrayList<>();
        for (Accounts accounts: accounts) {
            if (accounts!= null) {
                if (!classroom.getStudents().contains(accounts.getId())) {
                    arrayList.add(accounts);
                }
            }

        }
        return arrayList;
    }

}
