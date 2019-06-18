package io.token.rpc.client.healthchecks;

import io.opencensus.common.Clock;
import io.opencensus.stats.Aggregation.Count;
import io.opencensus.stats.Measure;
import io.opencensus.stats.Measure.MeasureLong;
import io.opencensus.stats.Stats;
import io.opencensus.stats.StatsRecorder;
import io.opencensus.stats.View;
import io.opencensus.stats.ViewManager;
import io.opencensus.tags.TagContext;
import io.opencensus.tags.TagKey;
import io.opencensus.tags.TagValue;
import io.opencensus.tags.Tagger;
import io.opencensus.tags.Tags;

import java.util.Collections;

final class HealthCheckMetrics {
    private static final TagKey CLIENT_TAG_KEY = TagKey.create("client");
    private static final String COUNT = "1";
    private static final StatsRecorder statsRecorder = Stats.getStatsRecorder();
    private static final ViewManager viewManager = Stats.getViewManager();
    private static final Tagger tagger = Tags.getTagger();
    private final MeasureLong success;
    private final MeasureLong errors;
    private final TagValue clientTagValue;

    private HealthCheckMetrics(MeasureLong success, MeasureLong errors, TagValue clientTagValue) {
        this.success = success;
        this.errors = errors;
        this.clientTagValue = clientTagValue;
    }

    /**
     * Create transactor metric instance.
     *
     * @param name name of metric
     * @return {@link HealthCheckMetrics} instance
     */
    static HealthCheckMetrics create(String name) {
        MeasureLong success = MeasureLong.create(
                "health/success_total",
                "The number of successful requests",
                COUNT);
        registerView(success);

        MeasureLong errors = MeasureLong.create(
                "health/errors_total",
                "The number of errors encountered",
                COUNT);
        registerView(errors);

        return new HealthCheckMetrics(success, errors, TagValue.create(name));
    }

    /**
     * Records request success.
     */
    void onSuccess() {
        statsRecorder
                .newMeasureMap()
                .put(success, 1L)
                .record(createTags());
    }

    /**
     * Records request error.
     */
    void onError() {
        statsRecorder
                .newMeasureMap()
                .put(errors, 1L)
                .record(createTags());
    }

    /**
     * Registers {@link Measure} and {@link View} if they weren't already and enables stats
     * collection. If the measure was already registered, then it's going to be reused.
     *
     * @param measure measure to be registered
     *
     * @see io.opencensus.implcore.stats.MeasureToViewMap#registerView(View, Clock)
     */
    private static void registerView(Measure measure) {
        viewManager.registerView(
                View.create(
                        View.Name.create("token/" + measure.getName()),
                        measure.getDescription(),
                        measure,
                        Count.create(),
                        Collections.<TagKey>singletonList(CLIENT_TAG_KEY)));
    }

    private TagContext createTags() {
        return tagger
                .currentBuilder()
                .put(CLIENT_TAG_KEY, clientTagValue)
                .build();
    }
}
