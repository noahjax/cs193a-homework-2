package com.example.noahjackson.todo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

//Basic to do list
//Type word into the editText and hit add to add it to the list
//Delete words by long clicking add, which will open edit mode. Long clicking an item here will
//select it, and clicking delete will remove the item. Long click delete to exit edit mode
public class toDo extends AppCompatActivity implements Button.OnClickListener, Button.OnLongClickListener, AdapterView.OnItemLongClickListener {

    private ArrayList<String> toDoList;
    private ArrayAdapter<String> toDoAdapter;
    private boolean edit;
    private int selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        edit = false;      //initiate edit mode to be off

        //initiate list and adapter
        toDoList = new ArrayList<String>();
        toDoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,toDoList);

        //Initiate Scanner to read in file
        Scanner scan = null;
        try {
            scan = new Scanner(openFileInput("output.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String cur = "";
        while (scan != null && scan.hasNextLine())
        {
            cur = scan.nextLine();
            toDoAdapter.add(cur);
        }

        ListView toDoList = (ListView) findViewById(R.id.toDoList);
        toDoList.setOnItemLongClickListener(this);
        toDoList.setAdapter(toDoAdapter);

        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(this);
        addButton.setOnLongClickListener(this);
    }

    @Override   //Saves all of the data to output.txt
    protected void onPause()
    {
        super.onPause();
        PrintStream output = null;
        try {
            output = new PrintStream(openFileOutput("output.txt", MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < toDoList.size(); i++)
        {
            output.println(toDoList.get(i));
        }
        output.close();
    }

    //Either adds or deletes an item depending on which mode user is in
    @Override
    public void onClick(View v) {
        if(!edit){
            Button addButton = (Button) findViewById(R.id.addButton);
            EditText addText = (EditText) findViewById(R.id.textToAdd);
            String text = addText.getText().toString();
            if(!text.isEmpty()){
                toDoAdapter.add(text);
                addText.setText("");
            }
            toDoAdapter.notifyDataSetChanged();
        }
        else if(selected >= 0){
            String removing = toDoAdapter.getItem(selected);
            toDoAdapter.remove(toDoAdapter.getItem(selected));
            selected = -1;
            Toast.makeText(this,"You Deleted " + removing, Toast.LENGTH_SHORT).show();
        }
    }

    //Changes between edit mode and add mode
    @Override
    public boolean onLongClick(View v) {
        Button addButton = (Button) findViewById(R.id.addButton);
        edit = !edit;
        if(edit) {
            addButton.setText("Delete");
            EditText type = (EditText) findViewById(R.id.textToAdd);
            type.setVisibility(View.GONE);
        }
        else
        {
            addButton.setText("Add");
            EditText type = (EditText) findViewById(R.id.textToAdd);
            type.setVisibility(View.VISIBLE);
        }
        return false;
    }

    //Select and item to be deleted in edit mode, or moves item down
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(edit){
            Toast.makeText(this,"selected "+ toDoAdapter.getItem(position), Toast.LENGTH_SHORT).show();
            selected = position;
        }
        else {
            int size = toDoList.size();
            if(position < size-1){
                String adding = toDoAdapter.getItem(position).toString();
                toDoAdapter.remove(toDoAdapter.getItem(position));
                toDoAdapter.add(adding);
            }
        }
        return false;
    }
}
