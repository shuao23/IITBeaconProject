package ipro239.iitbeaconproject.beacon;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

import ipro239.iitbeaconproject.R;

/**
 * Created by shuao23 on 3/29/2017.
 */

public class BeaconFilters {

    public static final int ALL_FLAGS  = ~0;
    public static final int NO_FLAGS = 0;

    private int flag = ALL_FLAGS;
    private List<String> filters;

    public BeaconFilters(Context context){
        filters = Arrays.asList(context.getResources().getStringArray(R.array.beacon_filters));
    }
    public BeaconFilters(Context context, int flag){
        this(context);
        this.flag = flag;
    }

    public void setFlag(int flag){
        this.flag = flag;
    }
    public void setFilter(String filterName){
        setFilter(getFilterIndex(filterName));
    }
    public void setFilter(int index){
        if(index < 0)
            return;
        flag |= (1<<index);
    }

    public void clearFilter(String filterName){
        clearFilter(getFilterIndex(filterName));
    }
    public void clearFilter(int index){
        if(index < 0)
            return;
        flag &= ~(1<<index);
    }

    public boolean isFilterFlagged(int index){
        if(index < 0)
            return false;
        return (flag & (1<<index)) != 0;
    }

    public boolean doesFlagOverlap(int flag){
        return (flag == 0 || (flag & this.flag) != 0);
    }

    public boolean allFlagCleared(){
        return flag == 0;
    }

    public int getFilterCount(){
        return filters.size() > 32 ? 32 : filters.size();
    }

    public int getFlag(){
        return flag;
    }

    public int getFilterIndex(String filterName){
        int index = filters.indexOf(filterName);
        return index >= 32 ? -1 : index;
    }

    public String findFilterByIndex(int index){
        return index >= 32 ? null : filters.get(index);
    }

    public static int getFilterValue(int index){
        if(index >= 32)
            return 0;

        return (1<<index);
    }
}
