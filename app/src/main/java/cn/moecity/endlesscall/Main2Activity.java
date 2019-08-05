package cn.moecity.endlesscall;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String,Object>> listItems;
    private String numberStr,mainStr;
    private Button backBtn;
    private Boolean isClosed=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        listView=findViewById(R.id.listView);
        updateList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mainStr=listItems.get(position).get("name").toString();
                numberStr = listItems.get(position).get("number").toString();
                isClosed=true;
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.putExtra("name",mainStr);
                intent.putExtra("number",numberStr);
                startActivity(intent);
                finish();
            }
        });
        backBtn=findViewById(R.id.button2);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClosed=true;
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (!isClosed){
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
        }
        super.onDestroy();
    }

    private void updateList(){
        listItems=new ArrayList<Map<String,Object>>();
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while(cursor.moveToNext()){

            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contact = cursor.getString(nameFieldColumnIndex);
            //取得电话号码
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);

            while(phone.moveToNext())
            {
                String phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //格式化手机号
                phoneNumber = phoneNumber.replace("-","");
                phoneNumber = phoneNumber.replace(" ","");
                Map<String,Object> listItem=new HashMap<String,Object>();
                listItem.put("icon",R.mipmap.ic_launcher_round);
                listItem.put("name",contact);
                listItem.put("number",phoneNumber);
                listItems.add(listItem);

            }

        }
        cursor.close();
        simpleAdapter = new SimpleAdapter(this, listItems,R.layout.listitem,
                new String[]{"icon","number","name"},
                new int[]{R.id.item_img,R.id.item_content,R.id.item_title});
        listView.setAdapter(simpleAdapter);

    }
}
