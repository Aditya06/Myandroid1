package hk.ust.cse.comp107x.chatclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muppala on 18/6/16.
 */
public class ContactArrayAdapter  extends ArrayAdapter<Contacts.FriendInfo> {
    private final Context context;
    private final List<Contacts.FriendInfo> friendInfoArrayList;

    public ContactArrayAdapter(Context context, List<Contacts.FriendInfo> friendInfoArrayList) {
        super(context, R.layout.friend_item, friendInfoArrayList);
        this.context = context;
        this.friendInfoArrayList = friendInfoArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View friendInfoView;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Change the layout based on who the message is from
        friendInfoView = inflater.inflate(R.layout.friend_item, parent, false);
        TextView friendName = (TextView) friendInfoView.findViewById(R.id.friendName);
        friendName.setText(friendInfoArrayList.get(position).name);
        TextView statusMsg = (TextView) friendInfoView.findViewById(R.id.statusMsg);
        statusMsg.setText(friendInfoArrayList.get(position).statusMsg);

        // This set of steps are used to load the friend's picture into the ImageView. We take
        // the help of the Picasso image downloading library to do this for us asynchronously
        // TODO load the image in the background asynchronously using Picasso library
        ImageView imageView = (ImageView) friendInfoView.findViewById(R.id.avatar);
        Picasso.with(context).load("file:///android_asset/"+friendInfoArrayList.get(position).imageURL).into(imageView);

        return friendInfoView;
    }
}
