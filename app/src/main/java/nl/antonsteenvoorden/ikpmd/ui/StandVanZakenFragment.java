package nl.antonsteenvoorden.ikpmd.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import nl.antonsteenvoorden.ikpmd.R;
import nl.antonsteenvoorden.ikpmd.adapter.VakkenAdapter;
import nl.antonsteenvoorden.ikpmd.model.Module;


public class StandVanZakenFragment extends Fragment {
    View rootView;
    Context context;
    ListView listAandacht;
    VakkenAdapter vakkenAdapter;

    private PieChart mChart;
    private int maxECTS;

    ArrayList<Entry> yValues;
    ArrayList<String> xValues;

    List<Module> modules;
    List<Module> vakkenAandacht;

    public StandVanZakenFragment() {
        yValues = new ArrayList<>();
        xValues = new ArrayList<>();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static StandVanZakenFragment newInstance() {
        StandVanZakenFragment fragment = new StandVanZakenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stand_van_zaken, container, false);
        listAandacht = (ListView) rootView.findViewById(R.id.stand_van_zaken_list);

        vakkenAandacht = new ArrayList<>();
        modules = new ArrayList<>();

        calculateECTS();
        initChart();
        getData();

        context = rootView.getContext();

        vakkenAdapter = new VakkenAdapter(context, R.layout.vakken_list_item, vakkenAandacht);
        listAandacht.setAdapter(vakkenAdapter);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        mChart.animateY(1500);
    }

    public void initChart() {
        mChart = (PieChart) rootView.findViewById(R.id.chart);
        mChart.setDescription("");
        mChart.setTouchEnabled(false);
        mChart.setDrawSliceText(true);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setHoleRadius(85);
        mChart.setCenterTextColor(Color.rgb(0,188,186));
        mChart.setCenterText("0/0 \n Studiepunten behaald");
        mChart.setCenterTextSize(20);
        mChart.getLegend().setEnabled(false);
        mChart.animateY(1500);
    }
    public void calculateECTS() {
        modules.clear();
        modules = Module.getAll();
        for(Module module : modules) {
            this.maxECTS += module.getEcts();
        }
    }
    public void getData() {
        int tmpEcts = 0;
        modules.clear();
        vakkenAandacht.clear();
        modules = Module.getAll();
        for(Module module : modules) {
            double tmpGrade = module.getGrade();
            if(tmpGrade >= 5.5) {
                tmpEcts += module.getEcts();
            }
            else if(tmpGrade > 1.0 && tmpGrade <= 5.4){
                vakkenAandacht.add(module);
            }
        }
        setData(tmpEcts);
    }

    private void setData(int aantal) {
        String label = (String) getString(R.string.stand_van_zaken_data);
        mChart.setCenterText(aantal + " / 60 \n"+ label );

        if(xValues.size() >= 2 && yValues.size() >= 2) {
            yValues.set(0, new Entry(aantal, 0));
            yValues.set(1, new Entry(maxECTS-aantal, 1));
        } else {
            yValues.add(new Entry(aantal, 0));
            yValues.add(new Entry(maxECTS - aantal, 1));
            xValues.add("Behaalde ECTS");
            xValues.add("Resterende ECTS");
        }
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(0 ,188,186)); // blue
        colors.add(Color.rgb(35 ,10,78)); // deep purple

        PieDataSet dataSet = new PieDataSet(yValues, "ECTS");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        PieData data = new PieData(xValues, dataSet);
        data.setDrawValues(false);
        data.setValueTextSize(0.0f);
        mChart.setData(data); // bind dataset aan chart.

        mChart.invalidate();  // Aanroepen van een redraw
        //redraw list view
        listAandacht.invalidate();
        Log.d("aantal ects ", Integer.toString(aantal));
    }

}
