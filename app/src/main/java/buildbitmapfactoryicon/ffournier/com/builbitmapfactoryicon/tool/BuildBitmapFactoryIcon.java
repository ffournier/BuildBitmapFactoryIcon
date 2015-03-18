package buildbitmapfactoryicon.ffournier.com.builbitmapfactoryicon.tool;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by florian on 02/03/15.
 * Class BuildBitmapFactory
 * Create Bitap by Resource String
 * COnstruct Bitmap by Contacts for circle
 */
public class BuildBitmapFactoryIcon {

    private static final String TAG = "BuildBitmapFactoryIcon";
    // colors int int
    private static int[] colors = new int[] {Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.RED, Color.GRAY, Color.YELLOW};
    // color background cropped
    private static String colorBackground = "#BAB399";
    // 5 % de marge
    private static final float MARGIN_CIRCLE = 0.05f;


    /**
     * get Bitmap Circle
     * @param context : context
     * @param contacts : the contacts associate to the circle
     * @param resDefaultIconCircle: default Icon resource circle
     * @param resDefaultIcon: default Icon resource
     * @return the new bitmap
     */
    public static Bitmap getBitmapCircle(Context context, List<Contact> contacts, Integer resDefaultIconCircle, Integer resDefaultIcon) {
        Bitmap bitmap;
        if (contacts != null && contacts.size() > 0) {
            //  init size bitmap
            int height = getSizeIcon(context);
            // define marge
            int margin = (int)(MARGIN_CIRCLE * height);
            int width = height;

            // create an empty bitmap
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bitmap = Bitmap.createBitmap(height, width, conf);
            Canvas canvas = new Canvas(bitmap);

            // TODO create to difference contact of circle, maybe background color too , don't know better
            Paint paintStroke = new Paint();
            paintStroke.setColor(Color.BLACK);
            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setStrokeWidth(2);
            canvas.drawRect(0, 0, width, height, paintStroke);


            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

            // define Rect
            Rect rectIn = new Rect();
            // define row and column we need
            int size = contacts.size() < MAX_SIZE ? contacts.size() : MAX_SIZE;
            int column = getColumn(size);
            int row = getRow(size, column);

            // init left and top
            int left = margin;
            int top = margin;
            int widthBitmap = (width - ( 2 + (column -1)) * margin) / column;
            int heightBitmap = (height - (2 + row - 1) * margin) / row;

            Paint paint = new Paint();
            paint.setFilterBitmap(false);

            // Loop Contact
            int index = 0;
            Contact contact;
            for (int i = 0; i < size; i++) {
                // get Bitmap Contact
                contact = contacts.get(i);

                Bitmap bitmapContact = getBitmap(context, contact, resDefaultIcon);


                // set Rect Square draw in bitmap
                int leftResize, topResize, widthResize, heightResize;
                if (widthBitmap > heightBitmap) {
                    leftResize = left + (widthBitmap - heightBitmap) / 2;
                    widthResize = leftResize + heightBitmap;
                    topResize = top;
                    heightResize = topResize + heightBitmap;
                } else {
                    leftResize = left;
                    widthResize = leftResize + widthBitmap;
                    topResize = top + (heightBitmap - widthBitmap) / 2;
                    heightResize = topResize + widthBitmap;
                }

                rectIn.set(leftResize, topResize, widthResize, heightResize);
                canvas.drawBitmap(bitmapContact, null, rectIn, paint);

                // move left and top
                index++;
                if (index >= column) {
                    left = margin;
                    top += heightBitmap + margin;
                    index = 0;
                } else {
                    left += widthBitmap + margin;
                }

            }
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resDefaultIconCircle);
        }
        return bitmap;
    }

    /**
     * Get Bitmap Contact
     * @param context : context
     * @param contact : the contact
     * @param resDefaultIcon: default Icon resource
     * @return bitmap
     */
    public static Bitmap getBitmap(Context context, Contact contact, int resDefaultIcon) {
        Bitmap bitmap = null;
        if (contact != null && contact.getNativeContactId() != null) {
            try {
                Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contact.getNativeContactId());
                InputStream stream = ContactsContract.Contacts.openContactPhotoInputStream(
                        context.getContentResolver(), uri);
                Bitmap bitmapContact = BitmapFactory.decodeStream(stream);
                if (bitmapContact != null) {
                    bitmap = getCroppedBitmap(context, bitmapContact);
                }
            } catch  (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        if (bitmap == null) {
            bitmap = BuildBitmapFactoryIcon.genererateDefaultIcon(context, contact, resDefaultIcon);
        }
        return bitmap;
    }

    /**
     * Bitmap to Treat
     * @param context : context
     * @param bitmapContact
     * @return
     */
    private static Bitmap getCroppedBitmap(Context context, Bitmap bitmapContact) {
        Bitmap sbmp;
        int radius = getSizeIcon(context);
        if(bitmapContact.getWidth() != radius || bitmapContact.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bitmapContact, radius, radius, false);
        else
            sbmp = bitmapContact;

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(colorBackground));
        canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
                sbmp.getWidth() / 2+0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);


        return output;
    }

    private static int MAX_SIZE = 9;

    /**
     * Get Size of Icon by Density of device
     * @return size
     */
    private static int getSizeIcon(Context context) {
        int size;
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                size = 32;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                size = 48;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                size = 72;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                size = 96;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
            default:
                size = 144;
                break;
        }
        return size;


    }



    /**
     * Get Number of Column we need
     * @param size : the size of list contact
     * @return column
     */
    private static int getColumn(int size) {
        Double value = Math.sqrt(size);
        return (int) Math.ceil(value);
    }

    /**
     * Get Number of Row we need
     * @param size : the size of contact list
     * @param column : the column found
     * @return
     */
    private static int getRow(int size, int column) {
        double value = ((double) size / column);
        return (int) Math.ceil(value);
    }

    /**
     * Genererate Default Contact
     * @param context : context
     * @param contact
     * @param resDefaultIcon: default Icon resource
     * @return the default birmap
     */
    private static Bitmap genererateDefaultIcon(Context context, Contact contact, int resDefaultIcon) {
        Bitmap bitmap;
        if (contact != null && contact.getName() != null && contact.getName().length() > 0) {
            //  init size bitmap
            int height = getSizeIcon(context);
            int width = height;

            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            bitmap = Bitmap.createBitmap(height, width, conf);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setColor(Color.TRANSPARENT);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawPaint(paint);

            // Random r = new Random();
            int low = 0;
            int high = colors.length - 1;
            // int colorId = r.nextInt(high-low) + low;
            int colorId = getSumName(contact.getName() != null ? contact.getName() : "U") % (colors.length - 1);

            paint.setColor(colors[colorId]);
            canvas.drawCircle(width / 2, height / 2, (height / 2) /** MARGIN_CIRCLE*/, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize((float) (width * 0.6));
            paint.setFakeBoldText(true);
            String name = "U";
            if (contact.getName() != null && contact.getName().length() > 0) {
                name = contact.getName();
            }
            canvas.drawText(name.substring(0, 1).toUpperCase(), (float) (width * 0.31), (float) (height * 0.73), paint);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resDefaultIcon);
        }
        return bitmap;
    }

    /**
     * Convert String to Sum Int
     * @param name
     * @return
     */
    private static int getSumName(String name) {
        int result = 0;
        for (int i = 0; i < name.length(); i++) {
            result += name.charAt(i) % 10; // for to have a reasonable value
        }
        return result;
    }

    public static class Contact {

        String name;
        Long nativeContactId;

        public Contact(String name, Long nativeContactId) {
            this.name = name;
            this.nativeContactId = nativeContactId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getNativeContactId() {
            return nativeContactId;
        }

        public void setNativeContactId(Long nativeContactId) {
            this.nativeContactId = nativeContactId;
        }
    }

    /**
     * Get Contact By Uri
     * @param context ; the context
     * @param contactUri : the uri of contact
     * @return
     */
    public static Contact getNativeContact(Context context, Uri contactUri) {
        // define the projection for retrieving contact
        String[] projection = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};

        // first build the list with only the phone contact using getAll but filtering on
        // hasPhoneNum
        String sort = ContactsContract.Contacts.DISPLAY_NAME;
        // define the cursor that will retrieve the data
        Cursor contactCursor =
                context.getContentResolver()
                        .query(contactUri != null ? contactUri : ContactsContract.Contacts.CONTENT_URI, projection, null , null, sort);
        // then define the data:
        // The column index of the element to retrieve
        int nameColIndex = contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
        int idColIndex = contactCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
        Contact contact = null;
        //find the element that has the uri contactUri
        if (contactCursor.moveToNext()) {

            // retrieve the values
            Long id = contactCursor.getLong(idColIndex);
            String name = contactCursor.getString(nameColIndex);
            contact = new Contact(name, id);
        }
        // close cursors
        contactCursor.close();
        // then return the list
        return contact;
    }

}
