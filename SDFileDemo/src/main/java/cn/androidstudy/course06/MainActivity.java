package cn.androidstudy.course06;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText etTitle, etContent;
    private boolean isGranted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTitle = (EditText) findViewById(R.id.title);
        etContent = (EditText) findViewById(R.id.content);

        grantedAndRequest();
    }
    //判断是否授权，如未授权，则申请授权
    private void grantedAndRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission  = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            //和下面语句等效
            //int permission  = checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
            //如果已授权
            if (permission == PackageManager.PERMISSION_GRANTED) {
                isGranted = true;
            }else{
                //未授权，弹对话框，申请授权
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }else{
            isGranted = true;
        }
    }
    /**
     * 处理权限请求结果
     *
     * @param requestCode
     *          请求权限时传入的请求码，用于区别是哪一次请求的
     *
     * @param permissions
     *          所请求的所有权限的数组
     *
     * @param grantResults
     *          权限授予结果，和 permissions 数组参数中的权限一一对应，元素值为两种情况，如下:
     *          授予: PackageManager.PERMISSION_GRANTED
     *          拒绝: PackageManager.PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                isGranted = true;
            }
        }
    }

    //保存文件
    public void saveFile(View view) {
        if(!isGranted){
            Toast.makeText(this, "未获得权限，请授权！", Toast.LENGTH_SHORT).show();
            grantedAndRequest();
            return;
        }
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();

        OutputStream out = null;
        BufferedWriter bw = null;
        // 判断是否插入SD卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String folderName = Environment.getExternalStorageDirectory()
                    .getPath() + "/myTest";
            File folder = new File(folderName);
            if (folder == null || !folder.exists()) {
                // 如果文件夹不存在，则创建
                folder.mkdir();
            }
            File saveFile = new File(folderName, title);

            try {
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }
                out = new FileOutputStream(saveFile);
                bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                bw.write(content);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(out != null){
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //根据文件名读取文件
    public void readFile(View view) {
        if(!isGranted){
            Toast.makeText(this, "未获得权限，请授权！", Toast.LENGTH_SHORT).show();
            grantedAndRequest();
            return;
        }
        String title = etTitle.getText().toString();

        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        BufferedReader br = null;
        // 判断是否插入SD卡
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            String folderName = Environment.getExternalStorageDirectory()
                    .getPath() + "/myTest";
            File folder = new File(folderName);
            if (folder == null || !folder.exists()) {
                // 如果文件夹不存在，则退出
                Toast.makeText(this, folderName + "文件夹不存在", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            File saveFile = new File(folderName, title);

            try {
                if (!saveFile.exists()) {
                    // 如果文件不存在，则退出
                    Toast.makeText(this, folderName + "/" + title + "文件夹不存在",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                in = new FileInputStream(saveFile);
                br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String tmp;
                while ((tmp = br.readLine()) != null) {
                    sb.append(tmp);
                    sb.append("\r\n");
                }
                etContent.setText(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(in != null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
