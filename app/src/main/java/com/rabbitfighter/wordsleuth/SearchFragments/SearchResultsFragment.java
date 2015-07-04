package com.rabbitfighter.wordsleuth.SearchFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.rabbitfighter.wordsleuth.Activities.ResultsListActivity;
import com.rabbitfighter.wordsleuth.Database.ResultsDbAdapter;
import com.rabbitfighter.wordsleuth.ListItems.ResultTypeItem;
import com.rabbitfighter.wordsleuth.R;
import com.rabbitfighter.wordsleuth.Utils.RobotoFontsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Search results fragment displays the result types.
 * A list populated by a custom list view (W.I.P.)
 *
 * @author Joshua Michael Waggoner <rabbitfighter@cryptolab.net>
 * @version 0.1 (pre-beta) 2015-06-17.
 * @link https://github.com/rabbitfighter81/SwipeNavExample (Temporary)
 * @see 'http://developer.android.com/guide/components/fragments.html'
 * @since 0.1
 */
public class SearchResultsFragment extends Fragment {

    public final static String TAG = "SearchResultsFragment";
    private final static int resultTypeAnagram = 0x0;
    private final static int resultTypeSubword = 0x1;
    private final static int resultTypeCombo = 0x2;
    private final static int[] resultTypes = {resultTypeAnagram, resultTypeSubword, resultTypeCombo};
    private List<ResultTypeItem> resultTypeItemList;

    // Query
    String query;

    // Database
    ResultsDbAdapter dbAdapter;

    // Vars
    View itemView;
    ResultTypeItem resultType;
    ImageView iv_success;
    TextView tv_resultType, tv_numMatches, tv_title, tv_query, tv_number_letters;
    View rootView;

    /**
     * On creation of fragment
     * @param savedInstanceState -  the saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Get query from bundle
        query =  getArguments().get("query").toString();
        dbAdapter = new ResultsDbAdapter(getActivity());
        resultTypeItemList = new ArrayList<>();

        super.onCreate(savedInstanceState);
    }

    /**
     * When the view gets created
     * @param inflater - the LayoutInflater object
     * @param container - The ViewGroup object
     * @param savedInstanceState - The Bundle
     * @return rootView
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Logger
        Log.i(TAG, "onCreateView called");
        // View
        rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
        // Populate the list
        populateResultTypeList();
        // Populate the list view with the list
        populateListView();
        // Control the callbacks from item clicks
        registerClickCallback(rootView);
        // Set component info
        tv_query = (TextView) rootView.findViewById(R.id.tv_query);
        tv_query.setText("\"" +query+ "\"");
        tv_number_letters = (TextView) rootView.findViewById(R.id.tv_length);
        tv_number_letters.setText("" + query.length() + " letters");
        tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        // Fonts
        tv_title.setTypeface(RobotoFontsHelper.getTypeface(rootView.getContext().getApplicationContext(), RobotoFontsHelper.roboto_black)); // Condensed Bold

         /* Return the root view */
        return rootView;

    }

    /* --------------- */
    /* --- Adapter --- */
    /* --------------- */

    /**
     * List adapter.
     * @see 'http://developer.android.com/reference/android/widget/ArrayAdapter.html'
     */
    public class ResultTypeListAdapter extends ArrayAdapter<ResultTypeItem> {
        /**
         * Constructor that calls super. TODO: Research
         */
        public ResultTypeListAdapter() {
            super(getActivity(), R.layout.item_result_type, resultTypeItemList);
            Log.i(TAG, "ResultTypeListAdapter constructor called super");
        }

        /**
         * These are the list views
         * @param position -  the position of the item
         * @param convertView -  the view passed in
         * @param parent -  the parent
         * @return the view
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView() called");

            // The view passed in may be null, just an F.Y.I.
            if ((itemView = convertView) == null) {
                Log.i(TAG, "Item view is null!");
                itemView = getActivity().getLayoutInflater().inflate(R.layout.item_result_type, parent, false);
            }
            // Find Result Type
            resultType = resultTypeItemList.get(position);

            // Result type
            String rt = resultType.getResultType().substring(0, 1).toUpperCase() + resultType.getResultType().substring(1) + " Results";
            tv_resultType = (TextView) itemView.findViewById(R.id.tv_resultType);
            tv_resultType.setText(rt);
            // Number of matches
            tv_numMatches = (TextView) itemView.findViewById(R.id.tv_numMatches);
            tv_numMatches.setText(String.valueOf(resultType.getNumMatches()) + " results found");

            // Return the view
            return itemView;

        }
    }

    /* --------------- */
    /* --- Methods --- */
    /* --------------- */

    /**
     * Opens the results list activity with query and result type bundled in intent
     * @param resultType the resultType to lookup
     */
    private void openResultListActivity(String resultType) {
        Intent intent = new Intent(getActivity(), ResultsListActivity.class);
        Bundle b = new Bundle();
        b.putString("query", query);
        b.putString("resultType", resultType);
        intent.putExtras(b);
        startActivity(intent);
    }

    /**
     * Populate the results item list
     */
    public void populateResultTypeList() {
        if (resultTypeItemList == null || resultTypeItemList.isEmpty()) {
            resultTypeItemList.add(new ResultTypeItem("anagram", dbAdapter.getNumberAnagrams(), R.mipmap.ic_action_good, R.mipmap.ic_action_new));
            resultTypeItemList.add(new ResultTypeItem("subword", dbAdapter.getNumberSubwords(), R.mipmap.ic_action_good, R.mipmap.ic_action_new));
            resultTypeItemList.add(new ResultTypeItem("combo", dbAdapter.getNumberCombos(), R.mipmap.ic_action_good, R.mipmap.ic_action_new));
        } else {
            resultTypeItemList.get(0).setNumMatches(dbAdapter.getNumberAnagrams());
            resultTypeItemList.get(1).setNumMatches(dbAdapter.getNumberSubwords());
            resultTypeItemList.get(2).setNumMatches(dbAdapter.getNumberCombos());
        }
    }

    /**
     * Populate the list view.
     */
    private void populateListView() {
        Log.i(TAG, "populateListView() called");
        // Vars
        ArrayAdapter<ResultTypeItem> adapter;
        ListView list;
        // Create adapter
        adapter = new ResultTypeListAdapter();
        // Assert not null

        // Set list component
        if (getView() != null) {
            list = (ListView) getView().findViewById(R.id.lv_results);
            // Assign te list adapter
            list.setAdapter(adapter);
        }

    }

    /* ---------------- */
    /* --- Callbacks --- */
    /* ---------------- */

    /**
     * Callback for item clicks.
     * @param rootView - the root view passed in.
     */
    private void registerClickCallback(final View rootView) {
        Log.i(TAG, "registerClickCallback() called");
        ListView list;
        // Assert the view not null
        if (rootView == null) throw new AssertionError();
        // Set the list view component
        list = (ListView) rootView.findViewById(R.id.lv_results);
        // Set listener
        list.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                /**
                 * When an item is clicked
                 * @param parent - the parent
                 * @param viewClicked - the view that was clicked
                 * @param position -  the position of the clicked item
                 * @param id - ???
                 */
                @Override
                public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                    ResultTypeItem clickedItem = resultTypeItemList.get(position);
                    openResultListActivity(clickedItem.getResultType().toString());
                }


            }
        );
    }

    /* -------------- */
    /* --- States --- */
    /* -------------- */

    /**
     * On resume
     */
    @Override
    public void onResume() {
        // Populate the list and list view again, then call super on resume
        populateResultTypeList();
        populateListView();
        super.onResume();
    }

    /**
     * On start
     */
    @Override
    public void onStart() {
        // Populate the list and list view again, then call super on resume
        populateResultTypeList();
        populateListView();
        super.onStart();
    }

}//EOF
