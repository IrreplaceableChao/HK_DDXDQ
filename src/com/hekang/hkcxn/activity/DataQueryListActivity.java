package com.hekang.hkcxn.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hekang.R;
import com.hekang.hkcxn.model.FileListBean;
import com.hekang.hkcxn.model.FileNameBean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据查询 第二个界面
 * Created by ACHAO on 2015/5/6.
 */
public class DataQueryListActivity extends Activity {
    private MyAdpater adpater = null;
    private List<String> list;

    private List<Integer> itemPosition = new ArrayList<Integer>();
    private List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
    private List<FileNameBean> fileNameList = new ArrayList<FileNameBean>();
    private boolean isChcekAll = true;
    private Boolean isxian = false;
    private ListView scroll_list;
    private Button btn_delete;
    private TextView textView;
    private Button xuanzhongall;
    private Button quxiaoall;
    private TextView textView1;
    private String path;
    private Button fanhuichaxun;
    private TextView SelectorTitle;// title
    private LinearLayout lbufen;
    private File SDFile;
    private File file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modelleizhong);

        findViewByid();

        setOnClick();

        init();
    }

    private void init() {

        textView1.setVisibility(View.GONE);
        list = new ArrayList<String>();
        SDFile = Environment.getExternalStorageDirectory();
        File sdPath = new File(SDFile.getAbsolutePath() + "/HKCX-SJ");
        file = sdPath;

        FileListBean.list = list1;
        adpater = new MyAdpater();
        getAllFiles(sdPath);

        scroll_list.setAdapter(adpater);

    }

    private void setOnClick() {
        xuanzhongall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isChcekAll) {
                    for (int count = 0; count < list1.size(); count++) {
                        ((CheckBox) scroll_list.getAdapter().getView(count, null, null).findViewById(R.id.CheckBox)).setChecked(true);
                    }
                    adpater.notifyDataSetChanged();
                    xuanzhongall.setVisibility(View.GONE);
                    quxiaoall.setVisibility(View.VISIBLE);

                    isChcekAll = false;
                }

            }
        });
        quxiaoall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                xuanzhongall.setVisibility(View.VISIBLE);
                quxiaoall.setVisibility(View.GONE);
                for (int count = 0; count < list1.size(); count++) {
                    ((CheckBox) scroll_list.getAdapter().getView(count, null, null).findViewById(R.id.CheckBox)).setChecked(false);
                }
                adpater.notifyDataSetChanged();
                isChcekAll = true;
            }
        });
        btn_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DataQueryListActivity.this);
                builder.setTitle("提示");
                builder.setCancelable(false); // 不响应back按钮
                builder.setMessage("        数据删除后不可恢复，确定删除该条数据吗？"); // 对话框显示内容
                // 设置按钮
                builder.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 遍历要删除的文件名集合。之后将其一个个的删除
                        for (String filePath : list) {
                            File file = new File(filePath);
                            // file.delete();
                            RecursionDeleteFile(file);
                        }
                        if (file.length() >= list.get(0).length()) {

                            for (String filePath : list) {
                                File file = new File(SDFile.getAbsoluteFile() + "/HKCX-SJ"
                                        + filePath.substring(filePath.lastIndexOf("/"), filePath.length()));

                                RecursionDeleteFile(file);
                            }
                        }
                        fileNameList.clear();
                        list1.clear();
                        final File SDFile = Environment.getExternalStorageDirectory();
                        File sdPath = new File(SDFile.getAbsolutePath() + "/HKCX-SJ");
                        getAllFiles(sdPath);
                        btn_delete.setEnabled(false);
                        adpater.notifyDataSetChanged();
                    }
                }).show();
            }

        });

        scroll_list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                isxian = true;
                file = new File(list1.get(position).get("filePath"));
                System.out.println(file);
                if (!file.isFile()) {
                    getAllFiles(file);
                }
            }
        });
        fanhuichaxun.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DataQueryListActivity.this.finish();
            }
        });

    }

    private void findViewByid() {
        SelectorTitle = (TextView) findViewById(R.id.selector_title);
        SelectorTitle.setText("数据查询");
        textView1 = (TextView) findViewById(R.id.textView1);
        scroll_list = (ListView) findViewById(R.id.scroll_listdata);
        btn_delete = (Button) findViewById(R.id.delete);
        xuanzhongall = (Button) findViewById(R.id.xuanzhongall);
        quxiaoall = (Button) findViewById(R.id.quxiaoall);
        fanhuichaxun = (Button) findViewById(R.id.fanhuichaxun);
    }

    /**
     * �ݹ�ɾ���ļ����ļ���
     *
     * @param file Ҫɾ��ĸ�Ŀ¼
     */
    public static void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    class MyAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list1.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list1.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            view = LayoutInflater.from(DataQueryListActivity.this).inflate(
                    R.layout.fen, null);
            TextView textView1 = (TextView) view.findViewById(R.id.item_datav1);
            TextView textView2 = (TextView) view.findViewById(R.id.item_datav2);
            TextView textView3 = (TextView) view.findViewById(R.id.item_datav3);
            TextView textView4 = (TextView) view.findViewById(R.id.item_datav4);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.CheckBox);
            textView1.setText(fileNameList.get(position).getDuihao());
            textView2.setText(fileNameList.get(position).getCeshen());
            textView3.setText(fileNameList.get(position).getTime());
            textView4.setText(fileNameList.get(position).getQuhao());
            if (list.contains(list1.get(position).get("filePath"))) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {

                    if (isChecked) {
                        list.add(list1.get(position).get("filePath"));
                        btn_delete.setEnabled(true);
                        btn_delete.setTextColor(getResources().getColor(R.color.black));
                        itemPosition.add(position);
                    } else {
                        list.remove(list1.get(position).get("filePath"));

                        for (int i = 0; i < itemPosition.size(); i++) {
                            if (itemPosition.get(i) == position) {
                                itemPosition.remove(i);
                            }
                        }

                        if (list.size() == 0) {
                            btn_delete.setEnabled(false);
                            btn_delete.setTextColor(getResources().getColor(R.color.gray));
                        }
                    }
                }
            });
            return view;
        }
    }

    private void getAllFiles(File path) {

        this.path = path.getAbsolutePath();
        if (path.listFiles() == null ){
            return;
        }
        if (path.listFiles().length > 0) {
            for (File file : path.listFiles()) {
                if (this.path.equals(SDFile.getAbsolutePath() + "/HKCX-SJ")) {
                    boolean isxls = false;
                    for (File a : file.listFiles()) {
                        if ((a.toString().substring(a.toString().length() - 3, a.toString().length())).equals("xls")) {
                            isxls = true;
                        }
                    }
                    if (isxls == false) {
                        continue;
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    /**判断是否为井号查询*/
                    if (getIntent().getBooleanExtra("isNum", false) == true) {

                        String intent_duihao = getIntent().getStringExtra("duihao");

                        String allFileName = file.getName();

                        FileNameBean fileNameBean = new FileNameBean();
                        String quhao = allFileName.substring(0, allFileName.indexOf("：")); // jinghao-ceshen-20150618

                        if (intent_duihao.equals(allFileName.substring(quhao.length() + 1, allFileName.indexOf("(")))) {

                            fileNameBean.setQuhao(quhao);

                            allFileName = allFileName.substring(quhao.length() + 1, allFileName.length());
                            String duihao = allFileName.substring(0, allFileName.indexOf("(")); // jinghao-ceshen-20150618
                            fileNameBean.setDuihao(duihao);

                            allFileName = allFileName.substring(duihao.length() + 1, allFileName.length());
                            String ceshen = allFileName.substring(0, allFileName.indexOf("米)"));
                            fileNameBean.setCeshen(ceshen);

                            allFileName = allFileName.substring(ceshen.length() + 2, allFileName.length());
                            String time = allFileName.substring(0, allFileName.length());
                            fileNameBean.setTime(time);
                            fileNameList.add(fileNameBean);

                            map.put("fileName", file.getName());
                            map.put("filePath", file.getAbsolutePath());
                            list1.add(map);
                        }
                    } else {

                        fileNameList.add(getFileNameBean(file));

                        map.put("fileName", file.getName());
                        map.put("filePath", file.getAbsolutePath());
                        list1.add(map);
                    }
                } else {

                    if ((file.toString().substring(file.toString().length() - 3, file.toString().length())).equals("xls")) {

                        Intent intent = new Intent("android.intent.action.VIEW");

                        intent.addCategory("android.intent.category.DEFAULT");

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Uri uri = Uri.fromFile(file);
                        intent.setDataAndType(uri, "application/msword");
                        startActivity(intent);
                    }
                }

            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private FileNameBean getFileNameBean(File file){
        int indexOf = 0;
        FileNameBean fileNameBean = new FileNameBean();

        String allFileName = file.getName();
        indexOf = allFileName.indexOf("：");
        String quhao = allFileName.substring(0, indexOf); // jinghao-ceshen-20150618
        fileNameBean.setQuhao(quhao);

        allFileName = allFileName.substring(quhao.length() + 1, allFileName.length());
        String duihao = allFileName.substring(0, allFileName.indexOf("(")); // jinghao-ceshen-20150618
        fileNameBean.setDuihao(duihao);

        allFileName = allFileName.substring(duihao.length() + 1, allFileName.length());
        String ceshen = allFileName.substring(0, allFileName.indexOf("米)"));
        fileNameBean.setCeshen(ceshen);

        allFileName = allFileName.substring(ceshen.length() + 2, allFileName.length());
        String time = allFileName.substring(0, allFileName.length());
        fileNameBean.setTime(time);
        return fileNameBean;
    }
    private long timeString2Long(String timeStr) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = format.parse(timeStr);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date.getTime();
    }
}
