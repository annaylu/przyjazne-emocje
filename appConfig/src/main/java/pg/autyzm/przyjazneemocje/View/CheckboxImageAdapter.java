package pg.autyzm.przyjazneemocje.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import pg.autyzm.przyjazneemocje.R;
import pg.autyzm.przyjazneemocje.lib.entities.Level;


/**
 * Created by joagi on 05.12.2017.
 */

public class CheckboxImageAdapter extends ArrayAdapter<GridCheckboxImageBean> {



    int layoutResourceId;
    GridCheckboxImageBean data[] = null;

    private List<GridCheckboxImageBean> rowBeanList;
    private Context context;
    private Level level;
    private boolean isForTest;

    public CheckboxImageAdapter(Context context, int layoutResourceId, GridCheckboxImageBean[] data, Level level, boolean isForTest) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.level = level;
        this.isForTest = isForTest;
    }
//TODO
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View row = convertView;
        final RowBeanHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RowBeanHolder();
            holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBoxImagesToChoose);
            holder.delete_photo_button = (ImageButton) row.findViewById(R.id.delete_photo);

            row.setTag(holder);
        } else {
            holder = (RowBeanHolder) row.getTag();
        }

        final GridCheckboxImageBean photoWithCheckBox = data[position];

        try {
            String root = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";

            File fileOut;
            Bitmap captureBmp;
            if(photoWithCheckBox.photoName.contains(".mp4")) {
                fileOut = new File(root + "FriendlyEmotions/Videos" + File.separator + photoWithCheckBox.photoName);
                captureBmp = ThumbnailUtils.createVideoThumbnail(fileOut.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            } else {
                fileOut = new File(root + "FriendlyEmotions/Photos" + File.separator + photoWithCheckBox.photoName);
                captureBmp = MediaStore.Images.Media.getBitmap(photoWithCheckBox.cr, Uri.fromFile(fileOut));
            }

            holder.imgIcon.setImageBitmap(captureBmp);
        } catch (Exception e) {
            System.out.println(e);
        }

        final CheckBox checkBox = holder.checkBox;
//ustawienie checkboxa od zdjęcia w zależności  od właściwości levela
        if(isForTest && !level.getPhotosOrVideosIdListInTest().isEmpty()) {
            checkBox.setChecked(level.getPhotosOrVideosIdListInTest().contains(photoWithCheckBox.getId()));
        } else {
            checkBox.setChecked(level.getPhotosOrVideosIdList().contains(photoWithCheckBox.getId()));
        }

        checkBox.setOnClickListener(new View.OnClickListener() {
            Integer photoId = photoWithCheckBox.getId();
            @Override
            public void onClick(View arg0) {
                if(checkBox.isChecked()) {
                    if(photoWithCheckBox.photoName.contains(".mp4")){
                        level.setPhotosOrVideosFlag("videos");
                    } else {
                        if(isForTest) {
                            level.addPhotoForTest(photoId);
                        } else {
                            level.addPhoto(photoId);
                        }
                    }
                } else{
                    if(isForTest) {
                        level.removePhotoForTest(photoId);
                    } else {
                        level.removePhoto(photoId);
                    }
                }
            }
        });

        //8****************************

        final ImageButton delete_photo_button = holder.delete_photo_button;

        //TODO PHOTO DELETION
        //System.out.println("photoname inicjalizacja zdec nie wiadomo kosz " + photoWithCheckBox.photoName);
        if (photoWithCheckBox.photoName.contains("_r_")) {
            holder.delete_photo_button.setVisibility(View.INVISIBLE);
        }
        //holder.imgIcon.get
//ustawienie buttona od zdjęcia w zależności  od właściwości levela
        /*if(isForTest && !level.getPhotosOrVideosIdListInTest().isEmpty()) {
            checkBox.setChecked(level.getPhotosOrVideosIdListInTest().contains(photoWithCheckBox.getId()));
        } else {*/
            //checkBox.setChecked(level.getPhotosOrVideosIdList().contains(photoWithCheckBox.getId()));
        //}

        delete_photo_button.setOnClickListener(new View.OnClickListener() {
            Integer photoId = photoWithCheckBox.getId();
            @Override
            public void onClick(View arg0) {

   //toForTest(photoId);
                    //System.out.println("Chcemuy usunąć żdjęcie OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO photoId " + photoId);
//wyświetlamy pytanie czy na pewno
               holder.imgIcon.setVisibility(View.INVISIBLE);
                holder.checkBox.setVisibility(View.INVISIBLE);
                holder.delete_photo_button.setVisibility(View.INVISIBLE);




                //row.setVisibility(View.INVISIBLE);
               //usuwamy zjecie z levela

                level.removePhoto(photoId);
                level.addPhotoToBePermanentlyDeleted(photoId,photoWithCheckBox.photoName);
//data[position] = data[position+1];
                //usuwamy zdjęcie z katalogu
            }
        });



       /* ImageButton button = (ImageButton) convertView.findViewById(R.id.button_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //wyświetlenie zdjęć dla wybranej emocji
                //updateEmotionsGrid(getLevel().getEmotions().get(position));
            }
        });*/

        //************************

        return row;
    }

    static class RowBeanHolder {
        public ImageView imgIcon;
        public CheckBox checkBox;
        public ImageButton delete_photo_button;
    }
}