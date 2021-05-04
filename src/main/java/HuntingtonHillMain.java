import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Huntington-Hill algorithm for US Congress seats allocation.
 */
public class HuntingtonHillMain {

    public static void main(String[] args) {
        final int congressSeatsCount = 435;

        List<StateSeat> seats = calculateCongressSeats(State.values(), congressSeatsCount);

        seats.sort(StateSeat.STATE_NAME_CMP_ASC);

        for (StateSeat singleStateSeat : seats) {
            System.out.printf("%s: %d%n", singleStateSeat.state, singleStateSeat.seats);
        }

    }

    public static List<StateSeat> calculateCongressSeats(State[] states, int congressSeatsCount) {
        // max queue
        PriorityQueue<StateSeat> queue = new PriorityQueue<>(StateSeat.CONGRESS_WEIGHT_CMP_DESC);

        // allocate 1 seat for each state
        for (State singleState : states) {
            queue.add(new StateSeat(singleState, 1));
        }

        final int leftSeats = congressSeatsCount - states.length;

        for (int i = 0; i < leftSeats; ++i) {
            StateSeat curStateSeat = queue.poll();
            assert curStateSeat != null;

            curStateSeat.incSeats();
            queue.add(curStateSeat);
        }

        return new ArrayList<>(queue);
    }

    private static final class StateSeat {

        final State state;
        int seats;

        StateSeat(State state, int seats) {
            this.state = state;
            this.seats = seats;
        }

        static final Comparator<StateSeat> CONGRESS_WEIGHT_CMP_DESC =
                Comparator.comparing(StateSeat::calculateWeight).reversed();

        static final Comparator<StateSeat> STATE_NAME_CMP_ASC =
                Comparator.comparing(StateSeat::getState);

        /**
         * Calculate Congress weight usign the following formula:
         *
         * weight = P / sqrt( r * (r+1)),
         * where P - state population, r - assigned Congress seats count
         */
        double calculateWeight() {
            return state.population / Math.sqrt(seats * (seats + 1));
        }

        void incSeats() {
            seats += 1;
        }

        public int getSeats() {
            return seats;
        }

        State getState() {
            return state;
        }

    }

    enum State {
        Alabama(4_903_185),
        Alaska(731_545),
        Arizona(7_278_717),
        Arkansas(3_017_825),
        California(39_512_223),
        Colorado(5_758_736),
        Connecticut(3_565_287),
        Delaware(973_764),
        Florida(21_477_737),
        Georgia(10_617_423),
        Hawaii(1_415_872),
        Idaho(1_787_065),
        Illinois(12_671_821),
        Indiana(6_732_219),
        Iowa(3_155_070),
        Kansas(2_913_314),
        Kentucky(4_467_673),
        Louisiana(4_648_794),
        Maine(1_344_212),
        Maryland(6_045_680),
        Massachusetts(6_949_503),
        Michigan(9_986_857),
        Minnesota(5_639_632),
        Mississippi(2_976_149),
        Missouri(6_137_428),
        Montana(1_068_778),
        Nebraska(1_934_408),
        Nevada(3_080_156),
        New_Hampshire(1_359_711),
        New_Jersey(8_882_190),
        New_Mexico(2_096_829),
        New_York(19_453_561),
        North_Carolina(10_488_084),
        North_Dakota(762_062),
        Ohio(11_689_100),
        Oklahoma(3_956_971),
        Oregon(4_217_737),
        Pennsylvania(12_801_989),
        Rhode_Island(1_059_361),
        South_Carolina(5_148_714),
        South_Dakota(884_659),
        Tennessee(6_833_174),
        Texas(28_995_881),
        Utah(3_205_958),
        Vermont(623_989),
        Virginia(8_535_519),
        Washington(7_614_893),
        West_Virginia(1_792_147),
        Wisconsin(5_822_434),
        Wyoming(578_759);

        private final int population;

        State(int population) {
            this.population = population;
        }
    }
}
