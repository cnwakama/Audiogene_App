package chibuzo.nwakama.audiogene_collection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by arthonsystechnologiesllp on 10/03/17.
 */
//BaseAdapter ArrayAdapter<FileWrapper>

public class CustomAdapter extends ArrayAdapter<FileWrapper> {

    //Activity activity;
    List<FileWrapper> users;
    LayoutInflater inflater;
    FileWrapper model;
    int position;

    //short to create constructer using command+n for mac & Alt+Insert for window

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<FileWrapper> objects) {
        super(context, resource, objects);
        inflater        = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    /**public CustomAdapter(Activity activity) {
        this.activity = activity;
    }*/

    /**public CustomAdapter(Activity activity, List<FileWrapper> users) {
        this.activity   = activity;
        this.users      = users;

        inflater        = activity.getLayoutInflater();
    }*/


    /*@Override
    public int getCount() {
        return users.size();
    }*/

    /**@Override
    public Object getItem(int i) {
        return i;
    }*/

    /**@Override
    public long getItemId(int i) {
        return i;
    }*/

    /**@Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_check_box){

        }
    }*/

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;

        if (view == null){

            view = inflater.inflate(R.layout.item_list_item, viewGroup, false);

            holder = new ViewHolder();

            holder.tvUserName = (TextView) view.findViewById(R.id.tv_user_name);
            holder.ivCheckBox = (ImageView) view.findViewById(R.id.iv_check_box);

            view.setTag(holder);
        }else
            holder = (ViewHolder)view.getTag();

        model = getItem(i);

        holder.tvUserName.setText(model.getImage_name());
        holder.ivCheckBox.setBackgroundResource(R.drawable.images);

        /**holder.tvUserName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertadd = new AlertDialog.Builder(activity);
                LayoutInflater factory = LayoutInflater.from(activity);
                final View view2 = factory.inflate(R.layout.sample, null);
                ImageView photo = view2.findViewById(R.id.dialog_imageview);
                photo.setImageBitmap();
                alertadd.setView(view2);
                alertadd.setNeutralButton("Here!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }
                });

                alertadd.show();

            }
        });*/

        position = i;

        holder.ivCheckBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //model.isSelected = true;
                users.remove(position);
                notifyDataSetChanged();
            }
        });



        /**if (model.isSelected){
            users.remove(i);
            notifyDataSetChanged();
        }*/




        return view;

    }

    public void updateRecords(List<FileWrapper>  users){
        this.users = users;

        notifyDataSetChanged();
    }

    class ViewHolder{

        TextView tvUserName;
        ImageView ivCheckBox;

    }
}
