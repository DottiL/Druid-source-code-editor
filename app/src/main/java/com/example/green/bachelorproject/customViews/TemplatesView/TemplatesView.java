package com.example.green.bachelorproject.customViews.TemplatesView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.green.bachelorproject.R;
import com.example.green.bachelorproject.events.TemplateEvent;
import java.util.List;
import de.greenrobot.event.EventBus;
import utils.Syntax;

/**
 * Created by Green on 31/05/15.
 */
public class TemplatesView extends RelativeLayout {
    private GridView statementsGrid;
    private GridView keywordsGrid;
    private GridView operatorsGrid;
    private GridView typesGrid;

    private View statementsButton;
    private View keywordsButton;
    private View operatorsButton;
    private View typesButton;

    private View currentButton;
    private View currentGrid;

    public TemplatesView(Context context) {
        super(context);
        init(context);
    }

    public TemplatesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TemplatesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View v;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = layoutInflater.inflate(R.layout.templates_layout, this);

        statementsGrid = (GridView) v.findViewById(R.id.templates_statements_grid);
        keywordsGrid = (GridView) v.findViewById(R.id.templates_keywords_grid);
        operatorsGrid = (GridView) v.findViewById(R.id.templates_operators_grid);
        typesGrid = (GridView) v.findViewById(R.id.templates_types_grid);

        statementsButton = v.findViewById(R.id.templates_statements_button);
        keywordsButton = v.findViewById(R.id.templates_keywords_button);
        operatorsButton = v.findViewById(R.id.templates_operators_button);
        typesButton = v.findViewById(R.id.templates_types_button);
        currentButton = statementsButton;
        currentGrid = statementsGrid;
        statementsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentButton != null) {
                    currentButton = statementsButton;
                    currentGrid.setVisibility(View.GONE);
                    currentGrid = statementsGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                } else {
                    currentButton = statementsButton;
                    currentGrid = statementsGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                }
            }
        });

        keywordsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentButton != null) {
                    currentButton = keywordsButton;
                    currentGrid.setVisibility(View.GONE);
                    currentGrid = keywordsGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                } else {
                    currentButton = keywordsButton;
                    currentGrid = keywordsGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                }
            }
        });

        operatorsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentButton != null) {
                    currentButton = operatorsButton;
                    currentGrid.setVisibility(View.GONE);
                    currentGrid = operatorsGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                } else {
                    currentButton = operatorsButton;
                    currentGrid = operatorsGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                }
            }
        });

        typesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentButton != null) {
                    currentButton = typesButton;
                    currentGrid.setVisibility(View.GONE);
                    currentGrid = typesGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                } else {
                    currentButton = typesButton;
                    currentGrid = typesGrid;
                    currentGrid.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void initTemplate(Syntax s) {
        final List<String> statements = s.getStatementsList();
        statements.add(s.getMultiLineCommentDel());
        statements.add(s.getSingleLineCommentDel());
        statements.addAll(s.getStringDel());
        final List<String> keywords = s.getKeywords();
        final List<String> operators = s.getOperators();
        final List<String> types = s.getPrimitiveTypes();

        int count = 0;
//        statementsGrid.setAdapter(new TemplatesAdapter(getContext(), statements));
        statementsGrid.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.templates_item ,statements));

        statementsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view;
                EventBus.getDefault().post(new TemplateEvent(v.getText().toString()));
            }
        });

        for(String ss: statements) {
            if(ss.length() > count) {
                count = ss.length();
            }
        }
        statementsGrid.setColumnWidth(count*15+4);

//        keywordsGrid.setAdapter(new TemplatesAdapter(getContext(), keywords));
        keywordsGrid.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.templates_item ,keywords));

        keywordsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view;
                EventBus.getDefault().post(new TemplateEvent(v.getText().toString()));
            }
        });

        for(String ss: keywords) {
            if(ss.length() > count) {
                count = ss.length();
            }
        }
        keywordsGrid.setColumnWidth(count*15+4);

//        operatorsGrid.setAdapter(new TemplatesAdapter(getContext(), operators));
        operatorsGrid.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.templates_item , operators));

        operatorsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view;
                EventBus.getDefault().post(new TemplateEvent(v.getText().toString()));
            }
        });

        for(String ss: operators) {
            if(ss.length() > count) {
                count = ss.length();
            }
        }

        operatorsGrid.setColumnWidth(count*15+4);

//        typesGrid.setAdapter(new TemplatesAdapter(getContext(), types));
        typesGrid.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.templates_item ,types));

        typesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view;
                EventBus.getDefault().post(new TemplateEvent(v.getText().toString()));
            }
        });

        for(String ss: types) {
            if(ss.length() > count) {
                count = ss.length();
            }
        }
        typesGrid.setColumnWidth(count*15+4);
    }

    private class TemplatesAdapter extends BaseAdapter {
        private Context context;
        private final List<String> values;

        public TemplatesAdapter(Context context, List<String> values) {
            this.context = context;
            this.values = values;
        }

        @Override
        public int getCount() {
            return values.size();
        }

        @Override
        public Object getItem(int position) {
            return values.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = convertView;
            if (v == null) {
                v = inflater.inflate(R.layout.templates_item, null);
                ((TextView) v).setText(values.get(position));
            }
            return v;
        }
    }
}
