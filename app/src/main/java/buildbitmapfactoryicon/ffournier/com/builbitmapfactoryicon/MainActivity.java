package buildbitmapfactoryicon.ffournier.com.builbitmapfactoryicon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import buildbitmapfactoryicon.ffournier.com.builbitmapfactoryicon.tool.BuildBitmapFactoryIcon;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int PICK_CONTACT_REQUEST = 1;

    ImageView imageViewContact1;
    ImageView imageViewContact2;
    ImageView imageViewContact3;
    ImageView imageViewContact4;
    ImageView imageViewCircle;

    ArrayList<BuildBitmapFactoryIcon.Contact> contacts;

    int pickBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = new ArrayList<BuildBitmapFactoryIcon.Contact>();
        contacts.add(new BuildBitmapFactoryIcon.Contact(null, null));
        contacts.add(new BuildBitmapFactoryIcon.Contact(null, null));
        contacts.add(new BuildBitmapFactoryIcon.Contact(null, null));
        contacts.add(new BuildBitmapFactoryIcon.Contact(null, null));

        imageViewContact1 = (ImageView) findViewById(R.id.bitmap_contact1);
        imageViewContact1.setOnClickListener(this);
        imageViewContact2 = (ImageView) findViewById(R.id.bitmap_contact2);
        imageViewContact2.setOnClickListener(this);
        imageViewContact3 = (ImageView) findViewById(R.id.bitmap_contact3);
        imageViewContact3.setOnClickListener(this);
        imageViewContact4 = (ImageView) findViewById(R.id.bitmap_contact4);
        imageViewContact4.setOnClickListener(this);

        imageViewCircle = (ImageView) findViewById(R.id.bitmap_circle);

        //updateBitmaps();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bitmap_contact1:
                pickBitmap = 0;
                break;
            case R.id.bitmap_contact2:
                pickBitmap = 1;
                break;
            case R.id.bitmap_contact3:
                pickBitmap = 2;
                break;
            case R.id.bitmap_contact4:
                pickBitmap = 3;
                break;
            default:
                pickBitmap = -1;
                break;

        }

        // call Picker
        Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PICK_CONTACT_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    BuildBitmapFactoryIcon.Contact contact = BuildBitmapFactoryIcon.getNativeContact(this, contactData);
                    if (contact != null) {
                        // update contact and bitmap
                        if (pickBitmap >= 0) {
                            contacts.set(pickBitmap, contact);
                            updateBitmaps();
                        } else {
                            // TODO error
                            Log.e(getClass().getCanonicalName(), "bitmap to change not found");
                        }
                    } else {
                        // TODO error
                        Log.e(getClass().getCanonicalName(), "Contact not found");
                    }
                }
                break;
        }
    }

    /**
     * Update Contact and circle
     */
    private void updateBitmaps() {
        // update Contact
        imageViewContact1.setImageBitmap(BuildBitmapFactoryIcon.getBitmap(this, contacts.get(0), R.drawable.default_icon));
        imageViewContact2.setImageBitmap(BuildBitmapFactoryIcon.getBitmap(this, contacts.get(1), R.drawable.default_icon));
        imageViewContact3.setImageBitmap(BuildBitmapFactoryIcon.getBitmap(this, contacts.get(2), R.drawable.default_icon));
        imageViewContact4.setImageBitmap(BuildBitmapFactoryIcon.getBitmap(this, contacts.get(3), R.drawable.default_icon));
        // update Circle
        imageViewCircle.setImageBitmap((BuildBitmapFactoryIcon.getBitmapCircle(this, contacts, R.drawable.default_icon, R.drawable.default_icon)));
    }
}
