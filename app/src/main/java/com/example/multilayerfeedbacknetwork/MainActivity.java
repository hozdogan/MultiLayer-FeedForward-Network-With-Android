package com.example.multilayerfeedbacknetwork;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    ArrayList<Paint> colorvec;
    ArrayList<Pattern2D> vec;
    float [][] Weights;
    float [][] Veights;
    Pattern2D pat;
    int clscount,classitemindex,HiddenNeuron;
    Integer cycle = 0;
    float LC1 = 0.5f,LC2 = 0.6f,alfa1 = 0.5f,alfa2 = 0.8f;
    float net,hata = 0, signalol,x,y,sumX = 0,sumY = 0,ortX,ortY,SSX,SSY,sumWeights = 0;
    boolean sgn,cont,normalize = false;
    float errorsum = 0.0f;
    Float texterror;
    class Pattern2D
    {
        private float X,Y,desired,bias,normX,normY;
        private int cid;
        public Pattern2D(float X,float Y)
        {
            this.X = X;
            this.Y = Y;
        }
        public void setX(float X)
        {
            this.X = X;
        }
        public void setY(float Y)
        {
            this.Y = Y;
        }
        public void setBias(float bias)
        {
            this.bias = bias;
        }
        public void setDesiredValue(float d)
        {
            desired = d;
        }
        public float getX()
        {
            return X;
        }
        public float getY()
        {
            return Y;
        }
        public float getDesiredValue()
        {
            return desired;
        }
        public float getBias()
        {
            return bias;
        }
        public int getClassID()
        {
            return cid;
        }
        public void setClassID(int id)
        {
            cid = id;
        }
        public void setnormX(float normX){this.normX = normX;}
        public void setnormY(float normY){this.normY = normY;}
        public float getnormX()
        {
            return normX;
        }
        public float getnormY()
        {
            return normY;
        }

    }
    ImageView pictureBox1;
    Spinner spinner1;
    Spinner spinner2;
    Spinner spinner3;
    TextView text1;
    TextView text2;
    Button button1;
    Button button2;
    Button button3;
    Button butonset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pictureBox1 = (ImageView)findViewById(R.id.imageView2);
        spinner1 = (Spinner)findViewById(R.id.spinner);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner3 = (Spinner)findViewById(R.id.spinner3);
        text1 = (TextView)findViewById(R.id.textView7);
        text2 = (TextView)findViewById(R.id.textView6);
        button1 = (Button)findViewById(R.id.button);
        button2 = (Button)findViewById(R.id.button4);
        button3 = (Button)findViewById(R.id.button3);


        Bitmap bitmap = Bitmap.createBitmap(350/*width*/, 350/*height*/, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        onDraw(canvas);
        pictureBox1.setImageBitmap(bitmap);

        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this,R.array.classcount,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,R.array.classid,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this,R.array.HiddenNeuronSize,android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        //spinnerlar secilince olacaklar
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String [] clsiddizisi = getResources().getStringArray(R.array.classcount);
                clscount = position+1;
                //Integer i = position+1;
                //String s = i.toString();
                //text1.setText(s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                classitemindex = position;
                //String [] clsiddizisi = getResources().getStringArray(R.array.classid);
                //Integer i = position;
                //String s = i.toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HiddenNeuron = position +1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setColorVec();
        TouchOn();
        ClickTrainingButton();
        clickNormalizeButton();
        ClickTestButton();



    }
    protected void onDraw(Canvas canvas)
    {
        Paint pBackground = new Paint();
        pBackground.setColor(Color.BLACK);
        pBackground.setStrokeWidth(5);
        canvas.drawLine(0,175,350,175,pBackground);
        canvas.drawLine(175,0,175,350,pBackground);

    }

    protected void DrawModelS(Canvas canvas, Paint paint, float X,float Y)
    {
        canvas.drawLine(X-4,Y,X+4,Y,paint);
        canvas.drawLine(X,Y-4,X,Y+4,paint);
    }
    protected void setColorVec()
    {
        colorvec = new ArrayList<Paint>();
        vec = new ArrayList<Pattern2D>();
        Paint pa1 = new Paint();
        pa1.setColor(Color.RED);
        Paint pa2 = new Paint();
        pa2.setColor(Color.BLUE);
        Paint pa3 = new Paint();
        pa3.setColor(Color.GREEN);
        Paint pa4 = new Paint();
        pa4.setColor(Color.MAGENTA);
        Paint pa5 = new Paint();
        pa5.setColor(Color.BLACK);
        Paint pa6 = new Paint();
        pa6.setColor(Color.YELLOW);
        colorvec.add(pa1); colorvec.add(pa2);
        colorvec.add(pa3); colorvec.add(pa4);
        colorvec.add(pa5); colorvec.add(pa6);
    }
    protected void setWeights()
    {
        Weights = new float[clscount][HiddenNeuron+1];
        Veights = new float[HiddenNeuron][3];

        Random r = new Random();

        for(int i = 0;i<clscount;i++)
        {
            for(int j=0;j<HiddenNeuron+1;j++)
            {

                Weights[i][j] = r.nextFloat();
            }
        }
        for(int ii=0;ii<HiddenNeuron;ii++)
        {
            for(int jj=0;jj<3;jj++)
            {
                Veights[ii][jj] = r.nextFloat();
            }
        }


    }
    protected void TouchOn()
    {

        pictureBox1.setOnTouchListener(new View.OnTouchListener() {
            Canvas canvas = new Canvas(((BitmapDrawable)pictureBox1.getDrawable()).getBitmap());
            @Override
            public boolean onTouch(View v, final MotionEvent event) {

                x = (event.getX()-pictureBox1.getWidth()/2)/2.61f;
                y = (pictureBox1.getHeight()/2-event.getY())/2.61f;
                Float f1 = x;
                Float f2 = y;
                text1.setText(f1.toString());
                text2.setText(f2.toString());
                DrawModelS(canvas,colorvec.get(classitemindex),event.getX()/2.61f,event.getY()/2.61f);
                pat = new Pattern2D(x,y);
                pat.setBias(1.0f);
                pat.setClassID(classitemindex);
                vec.add(pat);
                return false;
            }
        });

    }
    public float s_sapma(float [] a,int argc)
    {
        int ort, sum = 0, ortsum = 0;
        for (int i = 0; i < argc; i++) {
            sum += a[i];
        }
        ort = sum / argc;
        for (int i = 0; i < argc; i++) {
            ortsum += (a[i] - ort) * (a[i] - ort);
        }
        ortsum = ortsum / (argc);
        return (float) Math.sqrt(ortsum);
    }
    protected void clickNormalizeButton()
    {
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Geldi",Toast.LENGTH_SHORT).show();
                float [] normX = new float[vec.size()];
                float [] normY = new float[vec.size()];
                normalize = true;
                for (int i = 0; i < vec.size(); i++)
                {
                    normX[i] = vec.get(i).getX();
                    normY[i] = vec.get(i).getY();
                    sumX += vec.get(i).getX();
                    sumY += vec.get(i).getY();
                }
                ortX = sumX / vec.size(); ortY = sumY / vec.size();

                SSX = s_sapma(normX, vec.size());
                SSY = s_sapma(normY, vec.size());
                for (int j = 0; j < vec.size(); j++)
                {
                    vec.get(j).setnormX(((vec.get(j).getX() - ortX) / SSX));//*30
                    vec.get(j).setnormY(((vec.get(j).getY() - ortY) / SSY));

                }

            }
        });
    }
    protected void ClickTrainingButton()
    {
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setWeights();

                float [][] hloa = new float[HiddenNeuron+1][1];
                float [][] oloa = new float[clscount][1];
                float [][] VecX = new float[3][1];
                float [][] desiredArr = new float[clscount][1];
                float [][] tempW = new float[clscount][HiddenNeuron+1];
                float [][] tempV = new float[HiddenNeuron][3];

                for(int i = 0;i<clscount;i++)
                {
                    for(int j=0;j<HiddenNeuron+1;j++)
                    {

                        tempW[i][j] = 0.0f;
                    }
                }
                for(int ii=0;ii<HiddenNeuron;ii++)
                {
                    for(int jj=0;jj<3;jj++)
                    {
                        tempV[ii][jj] = 0.0f;
                    }
                }
                for (int rr = 0; rr < clscount; rr++) {

                    oloa[rr][0] = 0.0f;
                }
                for (int cc = 0; cc < HiddenNeuron; cc++) {

                    hloa[cc][0] = 0.0f;
                }
                hloa[HiddenNeuron][0] = 1.0f;
                //Training

                while(true)
                {
                    for(int val = 0;val<vec.size();val++)
                    {
                        if(normalize == true)
                        {
                            VecX[0][0] = vec.get(val).getnormX();
                            VecX[1][0] = vec.get(val).getnormY();
                            VecX[2][0] = vec.get(val).getBias();
                        }
                        else
                        {
                            VecX[0][0] = vec.get(val).getX();
                            VecX[1][0] = vec.get(val).getY();
                            VecX[2][0] = vec.get(val).getBias();
                        }
                        for(int kk=0;kk<clscount;kk++)
                        {
                            if(vec.get(val).getClassID() == kk)
                            {
                                desiredArr[kk][0] = 1.0f;
                            }
                            else if(vec.get(val).getClassID()!=kk)
                            {
                                desiredArr[kk][0] = -1.0f;
                            }
                        }

                        // output layer calculate
                        for (int row = 0; row < HiddenNeuron; row++)//ara katman cıkıs y0 y1 y2
                        {
                            for (int col = 0; col < 1; col++)
                            {
                                for (int op = 0; op < 3; op++)
                                {
                                    hloa[row][col] += Veights[row][op] * VecX[op][col];
                                }
                                hloa[row][col] =(float) Math.tanh(hloa[row][col]);//(float)((2.0f/(1+Math.exp(-hloa[row][col])))-1);
                            }
                        }

                        for (int row1 = 0; row1 < clscount; row1++)//cıkıs katman setlemesi o1 o2 o3
                        {
                            for (int col1 = 0; col1 < 1; col1++)//(float)((2.0f/(1+Math.exp(-oloa[row1][col1])))-1)
                            {
                                for (int op1 = 0; op1 < HiddenNeuron + 1; op1++)
                                {
                                    oloa[row1][col1] += Weights[row1][op1] * hloa[op1][col1];
                                }
                                oloa[row1][col1] = (float)Math.tanh(oloa[row1][col1]);//(float)((2.0f/(1+Math.exp(-oloa[row1][col1])))-1);
                            }
                        }


                        //deltaWeight guncelle
                        for (int k1 = 0; k1 < clscount; k1++)//scbak 3x5
                        {
                            signalol = LC1 * (desiredArr[k1][0] - oloa[k1][0]) * ((1-(oloa[k1][0]*oloa[k1][0]))/2);
                            hata += (desiredArr[k1][0] - oloa[k1][0])* (desiredArr[k1][0] - oloa[k1][0]);
                            for (int j1 = 0; j1 < HiddenNeuron + 1; j1++)
                            {
                                Weights[k1][j1] += (alfa1*signalol* hloa[j1][0])+(alfa2*tempW[k1][j1]);
                                tempW[k1][j1] = (signalol*hloa[j1][0]);
                            }
                        }
                        errorsum += hata;
                        hata = 0.0f;
                        //Hidden Layer Guncelle
                        for (int j = 0; j < HiddenNeuron; j++)//4x3
                        {
                            for (int i = 0; i < 3; i++)
                            {
                                for (int k = 0; k < clscount; k++)
                                {
                                    sumWeights += (desiredArr[k][0] - oloa[k][0])*((1-(oloa[k][0]*oloa[k][0]))/2)*Weights[k][j];
                                }
                                Veights[j][i] += (LC2 * ((1-(hloa[j][0]*hloa[j][0]))/2) * VecX[i][0] * sumWeights * alfa1)+(alfa2*tempV[j][i]);
                                tempV[j][i] = (LC2 * ((1-(hloa[j][0]*hloa[j][0]))/2) * VecX[i][0] * sumWeights);
                                sumWeights = 0.0f;

                            }
                        }
                        for (int i2 = 0; i2 < HiddenNeuron; i2++)
                        {
                            hloa[i2][0] = 0.0f;
                        }
                        for (int j2 = 0; j2< clscount; j2++)
                        {
                            oloa[j2][0] = 0.0f;
                        }

                    }
                    errorsum = (float) Math.sqrt(errorsum) / (clscount * vec.size());
                    if (errorsum < 0.01)
                        break;
                    errorsum = 0.0f;
                    cycle++;

                }
                text1.setText(cycle.toString());
                texterror = errorsum;
                text2.setText(texterror.toString());
                cycle = 0;
                errorsum = 0.0f;
            }
        });
    }

    protected void ClickTestButton()
    {

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap1 = Bitmap.createBitmap(350/*width*/, 350/*height*/, Bitmap.Config.ARGB_8888);
                float [][] hloatest = new float[HiddenNeuron+1][1];
                float [][] oloatest = new float[clscount][1];
                float [][] VecXtest = new float[3][1];
                float max;
                Float wf = pictureBox1.getWidth()/2.67556f;//350x350
                Float hf = pictureBox1.getHeight()/2.625f;
                int w = wf.intValue();
                int h = hf.intValue();

                int index;
                for (int row1 = 0; row1 < clscount; row1++) {

                    oloatest[row1][0] = 0.0f;
                }
                for (int row2 = 0; row2 < HiddenNeuron; row2++) {

                    hloatest[row2][0] = 0.0f;
                }
                hloatest[HiddenNeuron][0] = 1.0f;

                for(int row=0;row<h;row+=2)
                {
                    for(int col=0;col<w;col+=2)
                    {
                        VecXtest[0][0] = ((col - w/2)-ortX)/SSX;
                        VecXtest[1][0] = ((h/2-row)-ortY)/SSY;
                        VecXtest[2][0] = 1.0f;

                        //input network
                        for (int i = 0; i < HiddenNeuron; i++)//ara katman cıkıs y0 y1 y2
                        {
                            for (int j = 0; j < 1; j++)
                            {
                                for (int op = 0; op < 3; op++)
                                {
                                    hloatest[i][j] += Veights[i][op] * VecXtest[op][j];
                                }
                                hloatest[i][j] =(float) Math.tanh(hloatest[i][j]);
                            }
                        }

                        for (int ii = 0; ii < clscount; ii++)//cıkıs katman setlemesi o1 o2 o3
                        {
                            for (int jj = 0; jj < 1; jj++)
                            {
                                for (int op1 = 0; op1 < HiddenNeuron + 1; op1++)
                                {
                                    oloatest[ii][jj] += Weights[ii][op1] * hloatest[op1][jj];
                                }
                                oloatest[ii][jj] = (float)Math.tanh(oloatest[ii][jj]);
                            }
                        }

                        max = oloatest[0][0];
                        index = 0;
                        for (int ind = 0; ind < clscount; ind++)
                        {
                            if (oloatest[ind][0] > max)
                            {
                                max = oloatest[ind][0];
                                index = ind;
                            }
                        }

                        for (int i1 = 0; i1 < HiddenNeuron; i1++)
                        {
                            hloatest[i1][0] = 0.0f;
                        }
                        for (int i2 = 0; i2 < clscount; i2++)
                        {
                            oloatest[i2][0] = 0.0f;
                        }


                        //boyama kısmı
                        bitmap1.setPixel(col,row,colorvec.get(index).getColor());//colorvec.get(index).getColor()
                        pictureBox1.setImageBitmap(bitmap1);

                    }

                }
                Canvas canvas4 = new Canvas(bitmap1);
                for (int j = 0; j < vec.size(); j++)
                {
                    DrawModelS(canvas4,colorvec.get(vec.get(j).getClassID()),175+(vec.get(j).getX()),175-(vec.get(j).getY()));
                }
                onDraw(canvas4);


            }
        });
    }
}
