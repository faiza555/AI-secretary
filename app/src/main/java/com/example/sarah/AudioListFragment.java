 package com.example.sarah;

 import android.app.ProgressDialog;
 import android.content.Intent;
 import android.media.MediaPlayer;
 import android.net.Uri;
 import android.os.Build;
 import android.os.Bundle;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.annotation.RequiresApi;
 import androidx.constraintlayout.widget.ConstraintLayout;
 import androidx.fragment.app.Fragment;
 import androidx.navigation.NavController;
 import androidx.navigation.Navigation;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.RecyclerView;

 import android.os.Environment;
 import android.os.Handler;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.Button;
 import android.widget.ImageButton;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.SeekBar;
 import android.widget.TextView;
 import android.widget.Toast;

 import com.firebase.ui.database.FirebaseRecyclerAdapter;
 import com.firebase.ui.database.FirebaseRecyclerOptions;
 import com.firebase.ui.database.SnapshotParser;
 import com.google.android.gms.tasks.OnFailureListener;
 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.Query;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.storage.FirebaseStorage;
 import com.google.firebase.storage.StorageReference;

 import java.io.File;
 import java.io.IOException;
 import java.text.DateFormat;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Objects;


 /**
  * A simple {@link Fragment} subclass.
  */
 public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick {


     private RecyclerView audioList;
     private ConstraintLayout playerSheet;
     File[] allFiles;

     private AudioListAdapter audioListAdapter;
     private MediaPlayer mediaPlayer = null;
     private boolean isPlaying = false;

     private File fileToPlay = null;

     private ImageButton playBtn;
     private TextView playerHeader;
     private TextView playerFilename;
     private SeekBar playerSeekbar;
     private Handler seekbarHandler;
     private Runnable updateSeekbar;
     private static String mFileName = null;

     private Button note_btn;
     private NavController navController;

     public AudioListFragment() {
         // Required empty public constructor
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         //return inflater.inflate(R.layout.fragment_audio_list, container, false);

         View rootView= inflater.inflate(R.layout.fragment_audio_list,container,false);
         audioList= rootView.findViewById(R.id.audio_list_view);
         return rootView;

 }


     @Override
     public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
         audioList =view.findViewById(R.id.audio_list_view);
         playerSheet = view.findViewById(R.id.player_sheet);
         playBtn = view.findViewById(R.id.imageView3);
         playerHeader = view.findViewById(R.id.player_header_title);
         playerFilename = view.findViewById(R.id.textView5);
         playerSeekbar = view.findViewById(R.id.player_seekbar);

         navController = Navigation.findNavController(view);
         note_btn = view.findViewById(R.id.summarize_btn);
         //note_btn = view.findViewById(R.layout.single_list_item);

         //note_btn.setOnClickListener( audioListAdapter);

         mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();

        // String path = Objects.requireNonNull(requireActivity().getExternalFilesDir("/")).getAbsolutePath();
         String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
         File directory = new File(path);
         //File directory = new File(mFileName);
         allFiles = directory.listFiles();

         audioListAdapter = new AudioListAdapter(allFiles, this);

         audioList.setHasFixedSize(false);
         audioList.setLayoutManager(new LinearLayoutManager(getContext()));
         audioList.setAdapter(audioListAdapter);



        playBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseAudio();
                }else{
                    if(fileToPlay != null){
                        resumeAudio();
                    }

                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null) {
                    pauseAudio();
                }

            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlay != null) {
                    int progress = seekBar.getProgress();
                    mediaPlayer.seekTo(progress);
                    resumeAudio();
                }

            }
        });

     }

//     @Override
//     public void onClick(View v) {
//         switch (v.getId()) {
//             case R.id.summarize_btn:
//                 navController.navigate(R.id.action_audioListFragment_to_summarize);
//                 break;
//         }}


     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     @Override
     public void onClickListener(File file, int position) {
         fileToPlay = file;
         if(isPlaying)
         {
             stopAudio();
             playAudio(fileToPlay);

         } else {
             playAudio(fileToPlay);
         }

     }

     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     private void pauseAudio(){
         mediaPlayer.pause();
         playBtn.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
         isPlaying = false;
         seekbarHandler.removeCallbacks(updateSeekbar);
     }
     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     private void resumeAudio(){
         mediaPlayer.start();
         playBtn.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
         isPlaying = true;
         updateRunnable();
         seekbarHandler.postDelayed(updateSeekbar, 0);
     }


     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     private void stopAudio(){

         playBtn.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.player_play_btn, null));
         playerHeader.setText("Stopped");
         isPlaying = false;
         mediaPlayer.stop();
         seekbarHandler.removeCallbacks(updateSeekbar);


     }
     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     private void playAudio(File fileToPlay){

         mediaPlayer = new MediaPlayer();

         try {
             mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
             mediaPlayer.prepare();
             mediaPlayer.start();
         } catch (IOException e) {
             e.printStackTrace();
         }

         playBtn.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.list_pause_btn, null));
         playerFilename.setText(fileToPlay.getName());
         playerHeader.setText("Playing");


         isPlaying = true;

         mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
             @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
             @Override
             public void onCompletion(MediaPlayer mp) {
                 stopAudio();
                 playerHeader.setText("Paused");

             }
         });

         playerSeekbar.setMax(mediaPlayer.getDuration());
         seekbarHandler = new Handler();
         updateRunnable();
         seekbarHandler.postDelayed(updateSeekbar, 0);
     }

     private void updateRunnable() {
         updateSeekbar = new Runnable() {
             @Override
             public void run() {
                 playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                 seekbarHandler.postDelayed(this, 500);
             }
         };
     }

     @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     @Override
     public void onStop() {
         super.onStop();
         if(isPlaying){
             stopAudio();
         }
     }



 }
