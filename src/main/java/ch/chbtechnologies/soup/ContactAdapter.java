package ch.chbtechnologies.soup;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
    }


    public static class ViewHolder {

        private TextView tvName;
        private TextView tvPhone;
        private TextView tvAddress;
        private TextView tvLogo;
        private ImageView ivCall;
        private ImageView ivAdd;
        private ImageView ivAddress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvPhone = (TextView) convertView.findViewById(R.id.tvTel);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.tvLogo = (TextView) convertView.findViewById(R.id.tvLogo);
            viewHolder.ivCall = (ImageView) convertView.findViewById(R.id.ivCall);
            viewHolder.ivAdd = (ImageView) convertView.findViewById(R.id.ivAdd);
            viewHolder.ivAddress = (ImageView) convertView.findViewById(R.id.ivAddress);

            convertView.setTag( viewHolder);
        }
        else
            viewHolder = (ViewHolder) convertView.getTag();

        bindDetails(position, viewHolder);

        return convertView;
    }

    private void bindDetails(int position, ViewHolder viewHolder) {
        bindName(position, viewHolder.tvName);
        bindPhoneNo(position, viewHolder.tvPhone);
        bindAddress(position, viewHolder.tvAddress);
        bindLogo(position, viewHolder.tvLogo);
        bindCall(position, viewHolder.ivCall);
        bindAdd(position, viewHolder.ivAdd);
        bindAddressIcon(position, viewHolder.ivAddress);
    }

    private void bindAdd(final int position, ImageView ivAdd) {
        final ArrayList<String> phoneNumbers = MainActivity.getPhoneNumbers(getItem(position).getAddress());
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                intent.putExtra(ContactsContract.Intents.Insert.NAME, String.valueOf(getItem(position).getName()))
                        .putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumbers.get(1))
                        .putExtra(ContactsContract.Intents.Insert.POSTAL, String.valueOf(getItem(position).getPhoneNumber()))
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
                getContext().startActivity(intent);
            }
        });
    }

    private void bindCall(final int position, ImageView ivCall) {
        final ArrayList<String> phoneNumbers = MainActivity.getPhoneNumbers(getItem(position).getAddress());
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                phoneIntent.setData(Uri.parse("tel:" + phoneNumbers.get(1)));
                getContext().startActivity(phoneIntent);
            }
        });
    }

    private void bindLogo(int position, TextView tvLogo) {
        if(tvLogo != null) {
            if(getItem(position).getName().length()!=0) {
                char c = getItem(position).getName().charAt(0);
                tvLogo.setText(String.valueOf(getItem(position).getName().charAt(0)));
            }
            else
                tvLogo.setText("!");
        }
    }

    private void bindAddressIcon(int position, ImageView ivAddress){
        final String addresss = getItem(position).getPhoneNumber();
        ivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticClass.address = addresss;
                Intent i = new Intent(MainActivity.context, GMapActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
            }
        });
    }


    private void bindName(final int position, final TextView t) {
        if (t != null) {
            t.setText(getItem(position).getName());
           /* t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:" + getItem(position).getPhoneNumber()));
                    getContext().startActivity(phoneIntent);
                    Toast.makeText(getContext(), t.getText(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    private void bindAddress(int position, TextView tvAddress) {
        if (tvAddress != null)
            tvAddress.setText(getItem(position).getAddress());
    }

    private void bindPhoneNo(int position, TextView t) {
        if (t != null)
            t.setText(getItem(position).getPhoneNumber());
    }

}