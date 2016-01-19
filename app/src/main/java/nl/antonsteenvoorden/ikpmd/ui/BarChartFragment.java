package nl.antonsteenvoorden.ikpmd.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import butterknife.ButterKnife;
import nl.antonsteenvoorden.ikpmd.R;

/**
 * Created by Anton on 19/01/2016.
 */
public class BarChartFragment extends Fragment {
    private CombinedChart mChart;
    View rootView;
    Context context;

    public static BarChartFragment newInstance() {
        BarChartFragment fragment = new BarChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        super.onCreate(savedInstanceState);

        context = rootView.getContext();
        mChart = (CombinedChart) rootView.findViewById(R.id.chart);
        //  mChart.setDescription("lala");
        // mChart.se

        mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        setData();
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void setData(){

        // LINE DATA

        LineData line = new LineData();
        ArrayList<Entry> lineEntries = new ArrayList<>();

        for (int i = 0; i<10; i++){
            lineEntries.add(new Entry((float) Math.random() * 15, i));
        }

        LineDataSet lineDataSet = new LineDataSet(lineEntries,"Line DataSet");
        line.addDataSet(lineDataSet);

        // BAR DATA
        BarData bar = new BarData();
        ArrayList<BarEntry> barEntries = new ArrayList<BarEntry>();

        for (int i=0;i<10;i++) {
            barEntries.add(new BarEntry((float) Math.random()*15, i));
        }
        BarDataSet set = new BarDataSet(barEntries, "Bar DataSet");
        bar.addDataSet(set);

        // ADD data to the chart
        String[] xValues = {"x1","x2","x3","x4","x5","x6","x7","x8","x9","x10"};
        CombinedData data = new CombinedData(xValues);
        data.setData(line);
        data.setData(bar);

        mChart.setData(data);
        mChart.invalidate();
    }
}
