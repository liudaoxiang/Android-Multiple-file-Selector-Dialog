package paul.arian.fileselector;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.merge.MergeAdapter;

public class FolderSelectionActivity extends Activity {

    private static final String TAG = "FileSelection";
    private static final String FILES_TO_UPLOAD = "upload";
    private File mainPath = new File(Environment.getExternalStorageDirectory() + "");
    private ArrayList<File> resultFileList;

    private ListView directoryView;
    private ArrayList<File> directoryList = new ArrayList<File>();
    private ArrayList<String> directoryNames = new ArrayList<String>();
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> fileNames = new ArrayList<String>();
    Button ok, all, none;
    TextView path;

    Integer[] imageId = {
            R.drawable.document,
            R.drawable.document_gray,
            R.drawable.folder,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        directoryView = (ListView)findViewById(R.id.directorySelectionList);
        ok = (Button)findViewById(R.id.ok);
        all = (Button)findViewById(R.id.all);
        none = (Button)findViewById(R.id.none);
        TextView goUpView = (TextView)findViewById(R.id.goUpTextView);
        path = (TextView)findViewById(R.id.folderpath);
        goUpView.setClickable(true);

        all.setEnabled(false);
        none.setEnabled(false);

        loadLists();

        directoryView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position<directoryList.size()) {
                    mainPath = directoryList.get(position);
                    loadLists();
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ok();
            }
        });
    }

    public void onGoUpClickListener(View v){
        File parent = mainPath.getParentFile();
        Log.d(TAG, parent.toString());
        if(mainPath.equals(Environment.getExternalStorageDirectory())){
            Toast.makeText(this, "Can't exit external storage", Toast.LENGTH_SHORT).show();
        }else{
            mainPath = parent;
            loadLists();
        }
    }

    public void ok(){
        Intent result = this.getIntent();
        result.putExtra(FILES_TO_UPLOAD, mainPath);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void loadLists(){
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        FileFilter directoryFilter = new FileFilter(){
            public boolean accept(File file){
                return file.isDirectory();
            }
        };

        //if(mainPath.exists() && mainPath.length()>0){
            //Lista de directorios
            File[] tempDirectoryList = mainPath.listFiles(directoryFilter);
            directoryList = new ArrayList<File>();
            directoryNames = new ArrayList<String>();
            for(File file: tempDirectoryList){
                directoryList.add(file);
                directoryNames.add(file.getName());
            }
            ArrayAdapter<String> directoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, directoryNames);
            directoryView.setAdapter(directoryAdapter);

            //Lista de ficheros
            File[] tempFileList = mainPath.listFiles(fileFilter);
            fileList = new ArrayList<File>();
            fileNames = new ArrayList<String>();
            for(File file : tempFileList){
                fileList.add(file);
                fileNames.add(file.getName());
            }



            path.setText(mainPath.toString());
            iconload();
        //}
    }

    public void iconload(){
        String[] foldernames = new String[directoryNames.size()];
        foldernames = directoryNames.toArray(foldernames);

        String[] filenames = new String[fileNames.size()];
        filenames = fileNames.toArray(filenames);

        CustomListSingleOnly adapter1 = new CustomListSingleOnly(FolderSelectionActivity.this, directoryNames.toArray(foldernames), imageId[2]);
        CustomListSingleOnly adapter2 = new CustomListSingleOnly(FolderSelectionActivity.this, fileNames.toArray(filenames), imageId[1]);


        MergeAdapter adap = new MergeAdapter();

        adap.addAdapter(adapter1);
        adap.addAdapter(adapter2);


        directoryView.setAdapter(adap);
    }

}
