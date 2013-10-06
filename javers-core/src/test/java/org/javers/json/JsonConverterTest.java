package org.javers.json;

import org.fest.assertions.data.Offset;
import org.javers.test.assertion.Assertions;
import org.joda.time.LocalDateTime;
import org.junit.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author bartosz walacik
 */
public class JsonConverterTest {

    @Test
    public void shouldConvertIntToJson() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        String json = jsonConverter.toJson(12);

        //then
        Assertions.assertThat(json).isEqualTo("12");
    }

    @Test
    public void shouldConvertIntDoubleToJson() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        double value = 1/3.;
        String json = jsonConverter.toJson(value);

        //then
        Assertions.assertThat(json).isEqualTo("0.3333333333333333");
    }

    @Test
    public void shouldConvertIntDoubleFromJson() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        double value = jsonConverter.fromJson("0.3333333333333333", Double.class);

        //then
        Assertions.assertThat(value).isEqualTo(1/3., Offset.offset(0.0000000000000001));


        //{"iLocalMillis":1007245380000,"iChronology":{"iBase":{"iMinDaysInFirstWeek":4}}}
    }

    @Test
    public void shouldConvertLocalDateTimeToJsonInIsoFormat() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        LocalDateTime date = new LocalDateTime(2001,12,1,22,23,03);
        String json = jsonConverter.toJson(date);

        //then
        Assertions.assertThat(json).isEqualTo("\"2001-12-01T22:23:03\"");
    }

    @Test
    public void shouldConvertLocalDateTimeFromJsonInIsoFormat() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        LocalDateTime date = jsonConverter.fromJson("\"2001-12-01T22:23:03\"", LocalDateTime.class);

        //then
        Assertions.assertThat(date).isEqualTo(new LocalDateTime(2001,12,1,22,23,03));
    }

    @Test
    public void shouldConvertIntFromJson() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        int value = jsonConverter.fromJson("12",Integer.class);

        //then
        Assertions.assertThat(value).isEqualTo(12);
    }

    @Test
    public void shouldConvertNullToJson() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        String json = jsonConverter.toJson(null);

        //then
        Assertions.assertThat(json).isEqualTo("null");
    }

    @Test
    public void shouldConvertNullFromJson() {
        //given
        JsonConverter jsonConverter = new JsonConverter();

        //when
        Integer value = jsonConverter.fromJson("null", Integer.class);

        //then
        Assertions.assertThat(value).isNull();
    }
}
