package ir.pudica.test;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class DrugAdapter extends ArrayAdapter<Drug> implements SectionIndexer {

    HashMap<String, Integer> alphaIndexer;
    String[] sections;

    public DrugAdapter(Context context, Drug[] drugs) {
        super(context, 0, drugs);

        alphaIndexer = new HashMap<String, Integer>();
        int size = drugs.length;

        for (int x = 0; x < size; x++) {
            String s = drugs[x].getTitle();
            // get the first letter of the store
            String ch = s.substring(0, 1);
            // convert to uppercase otherwise lowercase a -z will be sorted
            // after upper A-Z
            ch = ch.toUpperCase();
            // put only if the key does not exist
            if (!alphaIndexer.containsKey(ch))
                alphaIndexer.put(ch, x);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        // create a list from the set to sort
        ArrayList<String> sectionList = new ArrayList<String>(
                sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sections = sectionList.toArray(sections);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Drug drug = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_drug, parent, false);
        }

        TextView textView = (TextView) convertView;

        SpannableStringBuilder builder = new SpannableStringBuilder();

        SpannableString title=  new SpannableString(drug.getTitle() + "\n");
        title.setSpan(new RelativeSizeSpan(2f),0,title.length(), 0);
        title.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),0,title.length(),0);
        builder.append(title);

        SpannableString subtitle = new SpannableString(drug.getSubTitle() + "\n\n");
        subtitle.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),0,subtitle.length(),0);
        builder.append(subtitle);

        SpannableString Rec1 = new SpannableString(drug.getRec1() + "\n");
        Rec1.setSpan(new ForegroundColorSpan(Color.parseColor("#552500")),0,26,0);
        Rec1.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),27,Rec1.length(),0);
        builder.append(Rec1);

        SpannableString Rec2 = new SpannableString(drug.getRec2() + "\n");
        Rec2.setSpan(new ForegroundColorSpan(Color.parseColor("#552500")),0,30,0);
        Rec2.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")),31,Rec2.length(),0);
        builder.append(Rec2);

        textView.setText(builder, TextView.BufferType.SPANNABLE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToDrug = new Intent(getContext(), ShowDrugs.class);
                goToDrug.putExtra("name", drug.getTitle());
                goToDrug.putExtra("class", "DPAL");
                getContext().startActivity(goToDrug);
            }
        });

        return convertView;
    }

    @Override
    public int getPositionForSection(int section) {
        return alphaIndexer.get(sections[section]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

}
