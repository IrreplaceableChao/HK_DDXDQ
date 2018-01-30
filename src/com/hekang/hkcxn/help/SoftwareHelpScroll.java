package com.hekang.hkcxn.help;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;

import com.hekang.R;

public class SoftwareHelpScroll extends Activity{
	
    ScrollView scroll;

//	RecyclerView recycleView;

//	MyRecyclerAdapter recycleAdapter;

//	private List<Integer> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_1);

//		ImageView imageView1 = (ImageView)findViewById(R.id.image_01);
//		ImageView imageView2 = (ImageView)findViewById(R.id.image_02);
//		ImageView imageView3 = (ImageView)findViewById(R.id.image_03);
//		ImageView imageView4 = (ImageView)findViewById(R.id.image_04);
//		ImageView imageView5 = (ImageView)findViewById(R.id.image_05);
//		ImageView imageView6 = (ImageView)findViewById(R.id.image_06);
//		ImageView imageView7 = (ImageView)findViewById(R.id.image_07);
//		ImageView imageView8 = (ImageView)findViewById(R.id.image_08);
//		ImageView imageView9 = (ImageView)findViewById(R.id.image_09);
//
//
//		imageView1.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl01));
//
//		imageView2.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl02));
//
//		imageView3.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl03));
//
//		imageView4.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl04));
//
//		imageView5.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl05));
//
//		imageView6.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl06));
//
//		imageView7.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl07));
//
//		imageView8.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl08));
//
//		imageView9.setImageBitmap(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl09));
//		mDatas = new ArrayList<Integer>();
//		mDatas.add(R.drawable.ddcl01);
//		mDatas.add(R.drawable.ddcl02);
//		mDatas.add(R.drawable.ddcl03);
//		mDatas.add(R.drawable.ddcl04);
//		mDatas.add(R.drawable.ddcl05);
//		mDatas.add(R.drawable.ddcl06);
//		mDatas.add(R.drawable.ddcl07);
//		mDatas.add(R.drawable.ddcl08);
//		mDatas.add(R.drawable.ddcl09);
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl02));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl03));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl04));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl05));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl06));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl07));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl08));
//		mDatas.add(decodeSampleBitmapFromResource(getResources(),R.drawable.ddcl09));
//		recycleView = (RecyclerView) findViewById(R.id.recycleListView);
//
//		recycleAdapter = new MyRecyclerAdapter(SoftwareHelpScroll.this,mDatas);
//
//		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//
//		recycleView.setLayoutManager(layoutManager);
//
//		layoutManager.setOrientation(OrientationHelper.VERTICAL);
//
//		recycleView.setAdapter(recycleAdapter);
//
//		recycleView.setItemAnimator(new DefaultItemAnimator());


		scroll = (ScrollView) findViewById(R.id.scroll);

    	scroll.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String status = getIntent().getStringExtra("status");
				if (status.equals("1")) {
					scroll.scrollTo(0, 0);
				}else if (status.equals("2")) {
					scroll.scrollTo(0, 9930);
				}else if (status.equals("3")) {
					scroll.scrollTo(0, 11480);
				}else if (status.equals("4")) {
					scroll.scrollTo(0, 18000);
				}
//				else if (status.equals("5")) {
//					scroll.scrollTo(0, 6720);
//				}else if (status.equals("6")) {
//					scroll.scrollTo(0, 9700);
//				}else if (status.equals("7")) {
//					scroll.scrollTo(0, 999999);
//				}
			}
		});
    	
//    	scroll.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				// TODO Auto-generated method stub
//				Log.e("scroll.getScaleY();", ""+scroll.getHeight());
//				return false;
//			}
//		});
	}



/**	class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>{

		private List<Integer> mDatas;
		private Context mContext;
		private LayoutInflater inflater;

		public MyRecyclerAdapter(Context context, List<Integer> datas){
			this.mContext = context;
			this.mDatas = datas;
			inflater = LayoutInflater.from(mContext);
		}
		//重写onCreateViewHolder方法,返回一个自定义的viewholder
		@Override
		public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

			View view = inflater.inflate(R.layout.recycler_image,parent,false);
			MyViewHolder holder = new MyViewHolder(view);
			return holder;
		}
		//填充onCreateViewHolder 方法返回的holde中的控件
		@Override
		public void onBindViewHolder(MyRecyclerAdapter.MyViewHolder holder, int position) {

//			holder.iv.setImageBitmap(decodeSampleBitmapFromResource(getResources(),mDatas.get(position)));

			int height=1000;

			switch (position){
				case 0:
					height=1970;
					break;
				case 1:
					height=1688;
					break;
				case 2:
					height=1236;
					break;
				case 3:
					height=1970;
					break;
				case 4:
					break;
				case 5:
					break;
				case 6:
					break;
				case 7:
					break;
				case 8:
					break;
				default:
					break;

			}
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
			holder.iv.setLayoutParams(ll);
			holder.iv.setImageResource(mDatas.get(position));
		}

		@Override
		public int getItemCount() {
			return mDatas.size();
		}


		class MyViewHolder extends RecyclerView.ViewHolder {

			ImageView iv;
			public MyViewHolder(View view){

				super(view);
				iv = (ImageView) view.findViewById(R.id.iv_item);

			}

		}
	}


	public static Bitmap decodeSampleBitmapFromResource(Resources res, int resID){
		//1.读取位图的尺寸与类型 (Read Bitmap Dimensions and Type)
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;  //在解码的时候避免内存的分配
		BitmapFactory.decodeResource(res,resID,options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;


//            String ImageType = options.outMimeType;
		//2.加载一个按比例缩小的版本到内存中(Load a Scaled Down Version into Memory)
		MyLogger.jLog().e("宽   高"+imageWidth+":"+imageHeight);
		options.inSampleSize = calculateInSampleSize(options,imageWidth, imageHeight);

		options.inJustDecodeBounds =false;//重新调用相关解码方法
		return BitmapFactory.decodeResource(res,resID,options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight){
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth){
			final int halfHeight = height /2;
			final int halfWidth = width/2;

			while((halfHeight/inSampleSize)>reqHeight && (halfWidth/inSampleSize)>reqWidth){
				inSampleSize *=2;
			}
		}
		return inSampleSize;
	}
		**/

}
