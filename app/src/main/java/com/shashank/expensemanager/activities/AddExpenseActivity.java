package com.shashank.expensemanager.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.shashank.expensemanager.R;
import com.shashank.expensemanager.transactionDb.AppDatabase;
import com.shashank.expensemanager.transactionDb.AppExecutors;
import com.shashank.expensemanager.transactionDb.TransactionEntry;
import com.shashank.expensemanager.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddExpenseActivity extends AppCompatActivity {

    TextInputEditText amountTextInputEditText;
    TextInputEditText descriptionTextInputEditText;
    TextInputLayout amountTextInputLayout;
    TextInputLayout descriptionTextInputLayout;
    TextView dateTextView;
    LinearLayout dateLinearLayout;
    Spinner categorySpinner;
    ArrayList<String> categories;
    Calendar myCalendar;

    private static AppDatabase appDatabase;


    //These variables contain data which will be stored permanently on hitting save button
    int amount;
    String description;
    Date dateOfExpense;
    String categoryOfExpense;       //This parameter is to decide category in a transaction
    String categoryOfTransaction;  //This parameter to decide whether it is income and expense

    //Variable to keep track from where it came to this activity
    String intentFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amountTextInputEditText=findViewById(R.id.amountTextInputEditText);
        descriptionTextInputEditText=findViewById(R.id.descriptionTextInputEditText);
        amountTextInputLayout=findViewById(R.id.amountTextInputLayout);
        descriptionTextInputLayout=findViewById(R.id.descriptionTextInputLayout);
        dateTextView=findViewById(R.id.dateTextView);
        dateLinearLayout=findViewById(R.id.dateLinerLayout);
        categorySpinner=findViewById(R.id.categorySpinner);

        appDatabase = AppDatabase.getInstance(this);

        categories = new ArrayList<>();

        myCalendar=Calendar.getInstance();
        setDateToTextView();

        //First task here is to determine from where this activity is launched from the 4 possibilities

        Intent intent=getIntent();
        intentFrom=intent.getStringExtra("from");

        if(intentFrom.equals(Constants.addIncomeString)){
            categoryOfTransaction=Constants.incomeCategory;
            setTitle("Add Income");
            categories.add("Income");
            categorySpinner.setClickable(false);
            categorySpinner.setEnabled(false);
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this,android.R.layout.simple_list_item_1,categories));

        }
        else if(intentFrom.equals(Constants.addExpenseString)) {
            categoryOfTransaction = Constants.expenseCategory;
            setTitle("Add Expense");
            categories.add("Food");
            categories.add("Travel");
            categories.add("Clothes");
            categories.add("Health");
            categories.add("Other");
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this,
                    android.R.layout.simple_list_item_1, categories));

        } else if(intentFrom.equals(Constants.editIncomeString)){
            setTitle("Edit Income");

            amountTextInputEditText.setText(String.valueOf(intent.getIntExtra("amount",0)));
            amountTextInputEditText.setSelection(amountTextInputEditText.getText().length());
            descriptionTextInputEditText.setText(intent.getStringExtra("description"));
            descriptionTextInputEditText.setSelection(descriptionTextInputEditText.getText().length());

            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date=sdf.parse(intent.getStringExtra("date"));
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateTextView.setText(intent.getStringExtra("date"));

            categoryOfTransaction=Constants.incomeCategory;
            categories.add("Income");
            categorySpinner.setClickable(false);
            categorySpinner.setEnabled(false);
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this,android.R.layout.simple_list_item_1,categories));

        } else if(intentFrom.equals(Constants.editExpenseString)){
            categoryOfTransaction=Constants.expenseCategory;
            setTitle("Edit Expense");
            amountTextInputEditText.setText(String.valueOf(intent.getIntExtra("amount",0)));
            amountTextInputEditText.setSelection(amountTextInputEditText.getText().length());
            descriptionTextInputEditText.setText(intent.getStringExtra("description"));
            descriptionTextInputEditText.setSelection(descriptionTextInputEditText.getText().length());
            dateTextView.setText(intent.getStringExtra("date"));
            SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date=sdf.parse(intent.getStringExtra("date"));
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            categories.add("Food");
            categories.add("Travel");
            categories.add("Clothes");
            categories.add("Health");
            categories.add("Other");
            categorySpinner.setAdapter(new ArrayAdapter<>(AddExpenseActivity.this,android.R.layout.simple_list_item_1,categories));
            categorySpinner.setSelection(categories.indexOf(intent.getStringExtra("category")));
        }

        dateLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

    }

    public void showDatePicker(){

        new DatePickerDialog(AddExpenseActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setDateToTextView();
            }
        },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setDateToTextView(){
        Date date=myCalendar.getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
        String dateToBeSet=sdf.format(date);
        dateTextView.setText(dateToBeSet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_expense_activty_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.saveButton:
                // COMPLETED: 10-09-2018 1.Retrieve and Save data to database and also update the recycler view


                if (amountTextInputEditText.getText().toString().isEmpty()
                        || descriptionTextInputEditText.getText().toString().isEmpty()) {

                    if (amountTextInputEditText.getText().toString().isEmpty())
                        amountTextInputEditText.setError("Amount cannot be empty");
                    if (descriptionTextInputEditText.getText().toString().isEmpty())
                        descriptionTextInputEditText.setError("Please write some description");

                } else {
                    amount = Integer.parseInt(amountTextInputEditText.getText().toString());
                    description = descriptionTextInputEditText.getText().toString();
                    dateOfExpense = myCalendar.getTime();

                    if (intentFrom.equals(Constants.addIncomeString)
                            || intentFrom.equals(Constants.editIncomeString))
                        categoryOfExpense = "Income";
                    else
                        categoryOfExpense = categories.get(categorySpinner.getSelectedItemPosition());


                    final TransactionEntry mTransactionEntry =
                            new TransactionEntry(amount, categoryOfExpense, description, dateOfExpense, categoryOfTransaction);
                    // COMPLETED: 13-09-2018  save to database
                    if (intentFrom.equals(Constants.addIncomeString) || intentFrom.equals(Constants.addExpenseString)) {
                        // COMPLETED: 13-09-2018 perform an insert operation to database
                        int transactionAmount = mTransactionEntry.getAmount();
                        Log.i("amount", String.valueOf(transactionAmount));
                        addTransactionToDb(mTransactionEntry);

                    } else if (intentFrom.equals(Constants.editIncomeString) || intentFrom.equals(Constants.editExpenseString)) {
                        // COMPLETED: 13-09-2018 perform a update operation to database

                        updateTransactionDetails(mTransactionEntry);

                    }

                    finish();
                }
                break;
        }

        return true;
    }

    public void updateTransactionDetails(final TransactionEntry transactionEntry) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.transactionDao().updateExpenseDetails(transactionEntry);
            }
        });
        Log.i("UPDATE",transactionEntry.getDescription());
    }

    public void addTransactionToDb(final TransactionEntry transactionEntry) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.transactionDao().insertExpense(transactionEntry);
            }
        });
        Log.i("gfvghdvhd",String.valueOf(transactionEntry.getAmount()));

    }

}
