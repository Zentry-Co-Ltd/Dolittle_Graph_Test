package kr.zentry.devdolittle_graph_hr_file_4.common;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.zentry.devdolittle_graph_hr_file_4.R;

public class ChartMarkerView extends MarkerView {
    private ArrayList<CandleEntry> candleEntryArrayList;
    private ArrayList<Long> timesArrayList;

    private Context context;

    private ViewGroup container_marker_view_chart;

    private TextView tv_marker_avg;
    private TextView tv_marker_sd;
    private TextView tv_marker_today;


    private boolean isHeart;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public ChartMarkerView(Context context, int layoutResource, CombinedChart chart, ArrayList<Long> timesArrayList, ArrayList<CandleEntry> candleEntryArrayList, boolean isHeart) {
        super(context, layoutResource);
        this.context = context;
        this.timesArrayList = timesArrayList;
        this.isHeart = isHeart;
        this.candleEntryArrayList = candleEntryArrayList;
        container_marker_view_chart = findViewById(R.id.container_marker_view_chart);
        tv_marker_avg = findViewById(R.id.tv_marker_avg);
        tv_marker_sd = findViewById(R.id.tv_marker_sd);
        tv_marker_today = findViewById(R.id.tv_marker_today);

        setChartView(chart);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (isHeart) {
            container_marker_view_chart.setBackground(context.getDrawable(R.drawable.round_light_red_nc_10));
        } else {
            container_marker_view_chart.setBackground(context.getDrawable(R.drawable.round_light_blue_nc_10));
        }

        double sd = candleEntryArrayList.get((int) e.getX()).getHigh() - e.getY();

        DecimalFormat decimalFormat = new DecimalFormat("##.#");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        tv_marker_today.setText(simpleDateFormat.format(timesArrayList.get((int) e.getX())));
        tv_marker_avg.setText(decimalFormat.format(e.getY()));
        tv_marker_sd.setText(decimalFormat.format(sd));


        super.refreshContent(e, highlight);
    }


    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 50); // place the midpoint of marker over the bar
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        super.draw(canvas, posX, posY);
        getOffsetForDrawingAtPoint(posX, posY);
    }
}
