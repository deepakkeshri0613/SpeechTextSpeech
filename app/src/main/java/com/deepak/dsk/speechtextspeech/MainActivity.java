package com.deepak.dsk.speechtextspeech;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.TextToSpeechCallback;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.List;

public class MainActivity extends Activity {

    private TextView speechToText;
    private Button recordBut;
    private Button textToSpeechBut;
    private EditText textToSpeech;
    SpeechProgressView speechProgressView;
    TypedArray typedArray;
    int[] colors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Speech.init(this, getPackageName());


        typedArray=getResources().obtainTypedArray(R.array.colours);
        colors=new int[typedArray.length()];
        for(int i=0;i<typedArray.length();++i)
            colors[i]=typedArray.getColor(i,0);

        speechToText=findViewById(R.id.convert_speech_text);
        recordBut=findViewById(R.id.record_speech);
        textToSpeechBut=findViewById(R.id.convert_text_speech_button);
        textToSpeech=findViewById(R.id.convert_text_speech);

        speechProgressView=findViewById(R.id.progress);
        speechProgressView.setColors(colors);

        recordBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                recordBut.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                recordBut.setTextColor(getResources().getColor(R.color.white));

                speechToText.setText("");
                try {
                    // you must have android.permission.RECORD_AUDIO granted at this point
                    Speech.getInstance().startListening(speechProgressView,new SpeechDelegate() {
                        @Override
                        public void onStartOfSpeech() {
                            Log.i("speech", "speech recognition is now active");
                        }

                        @Override
                        public void onSpeechRmsChanged(float value) {
                            Log.d("speech", "rms is now: " + value);
                        }

                        @Override
                        public void onSpeechPartialResults(List<String> results) {
                            StringBuilder str = new StringBuilder();
                            for (String res : results) {
                                str.append(res).append(" ");
                                speechToText.append(str);
                            }

                            Log.i("speech", "partial result: " + str.toString().trim());
                        }

                        @Override
                        public void onSpeechResult(String result) {
                            String msg;
                            Log.i("speech", "result: " + result);
                            speechToText.setText(result);

                            if(result.isEmpty())
                                msg="hey you have not Say any thing";
                            else
                                msg="you said "+result;

                            recordBut.setBackgroundColor(getResources().getColor(R.color.grey));
                            recordBut.setTextColor(getResources().getColor(R.color.black));
                            Speech.getInstance().say(msg, new TextToSpeechCallback() {
                                @Override
                                public void onStart() {
                                    Log.i("speech", "speech started");
                                }

                                @Override
                                public void onCompleted() {
                                    Log.i("speech", "speech completed");
                                }

                                @Override
                                public void onError() {
                                    Log.i("speech", "speech error");
                                }
                            });
                        }
                    });
                } catch (SpeechRecognitionNotAvailable exc) {
                    Log.e("speech", "Speech recognition is not available on this device!");
                    // You can prompt the user if he wants to install Google App to have
                    // speech recognition, and then you can simply call:
                    //
                    // SpeechUtil.redirectUserToGoogleAppOnPlayStore(this);
                    //
                    // to redirect the user to the Google App page on Play Store
                } catch (GoogleVoiceTypingDisabledException exc) {
                    Log.e("speech", "Google voice typing must be enabled!");
                }

            }
        });



        textToSpeechBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg;

                textToSpeechBut.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textToSpeechBut.setTextColor(getResources().getColor(R.color.white));
                msg=textToSpeech.getText().toString();
                if(msg.isEmpty())
                    msg="hey you have not enter any thing";

                Speech.getInstance().say(msg, new TextToSpeechCallback() {
                    @Override
                    public void onStart() {
                        Log.i("speech", "speech started");
                    }

                    @Override
                    public void onCompleted() {
                        Log.i("speech", "speech completed");
                        textToSpeechBut.setBackgroundColor(getResources().getColor(R.color.grey));
                        textToSpeechBut.setTextColor(getResources().getColor(R.color.black));
                    }

                    @Override
                    public void onError() {
                        Log.i("speech", "speech error");
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {

        // prevent memory leaks when activity is destroyed
        super.onDestroy();
        Speech.getInstance().shutdown();
    }
}
