package com.example.navigationwithtoolbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.webkit.WebView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String htmlText = " %s ";
        String myData = "<html><body style='text-align:justify;'><font size=\"4\">Praedico Portfolio Manager is designed for those who wants to learn the portfolio management and funds management. It allows you to do the dummy trading without investing a single penny. Praedico Portfolio Manager will also gives the advices on each stock along with dummy trading option. By practicing Praedico Portfolio Manager user will get knowledge about good stocks so that they can make more and more profit while doing the live trading. Praedico’s portfolio manager allows you to learn the management of portfolio and funds. As portfolio management and funds management are the most upcoming hot job fields. One can learn the management of portfolio and funds without investing and losing a single money. Also our portfolio manager provides a leader board where others can know how to manage the effective portfolio with the help of the best one. In all aspects our portfolio manager gives you an extra skill in your hands segregating you from the herd.</font></body></html>";
        WebView webView = findViewById(R.id.webView1);
        webView.loadData(String.format(htmlText, myData), "text/html", "utf-8");

    }
}
//this is new line