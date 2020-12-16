package com.viewpoints.aischeduler.ui.schedule;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.viewpoints.aischeduler.R;
import com.viewpoints.aischeduler.data.openapi.kma.PrecipitationType;
import com.viewpoints.aischeduler.data.openapi.kma.TownWeatherForecast;
import com.viewpoints.aischeduler.data.openapi.kma.UltrashortWeatherForecast;

import org.jetbrains.annotations.NotNull;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class WeatherForecastListAdapter extends RecyclerView.Adapter<WeatherForecastListAdapter.ViewHolder> {
    protected List<UltrashortWeatherForecast> items;

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected final ImageView skyImage, precipitationImage;
        protected final TextView datetimeText, temperatureText, skyText, precipitationText, windText;

        public ViewHolder(View view) {
            super(view);
            datetimeText = view.findViewById(R.id.datetime_text);

            skyImage = view.findViewById(R.id.sky_image);
            skyText = view.findViewById(R.id.sky_text);
            temperatureText = view.findViewById(R.id.temperature_text);
            precipitationImage = view.findViewById(R.id.precipitation_image);
            precipitationText = view.findViewById(R.id.precipitation_text);
            windText = view.findViewById(R.id.wind_text);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void bind(UltrashortWeatherForecast item) {
            datetimeText.setText(item.getDateTime().format(DateTimeFormatter.ofPattern("d일 a h시").withLocale(Locale.KOREA)));

            temperatureText.setText(String.format("%.1f℃", item.getTemperature()));

            int image;
            boolean precipiation = false;

            switch (item.getPrecipitationType()) {
                case RAIN:
                    image = R.drawable.db05;
                    precipiation = true;
                    break;
                case SLEET:
                    image = R.drawable.db06;
                    precipiation = true;
                    break;
                case SNOW:
                    image = R.drawable.db08;
                    precipiation = true;
                    break;
                case SHOWERS:
                    image = R.drawable.db09;
                    precipiation = true;
                    break;
            }

            if (!precipiation) {
                switch (item.getSkyType()) {
                    case CLEAR:
                        image = item.getDateTime().getHour() >= 6 && item.getDateTime().getHour() < 18 ? R.drawable.db01 : R.drawable.db01_n;
                        break;
                    case CLOUDY:
                        image = item.getDateTime().getHour() >= 6 && item.getDateTime().getHour() < 18 ? R.drawable.db03 : R.drawable.db03_n;
                        break;
                    default:
                        image = R.drawable.db04;
                        break;
                }
            }

            if (item.getPrecipitationType() != PrecipitationType.NONE) {
                skyText.setText(String.format("%s %.1f㎜", item.getPrecipitationType(), item.getPrecipitationAmount()));
            } else {
                skyText.setText(item.getSkyType().toString());
            }

            if (item instanceof TownWeatherForecast)
            {
                TownWeatherForecast temp = (TownWeatherForecast)item;
                precipitationText.setText(temp.getPrecipitationProbability() + "%");

                precipitationImage.setVisibility(View.VISIBLE);
                precipitationText.setVisibility(View.VISIBLE);
            }

            windText.setText(String.format("%s %.0fmph", item.getWindDirectionType(), item.getWindSpeed()));
        }
    }

    public WeatherForecastListAdapter(List<UltrashortWeatherForecast> items) {
        this.items = items;
    }

    @NotNull
    @Override
    public WeatherForecastListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weather_forecast_item, viewGroup, false);
        return new WeatherForecastListAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(WeatherForecastListAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
