package com.netlab.vc.coursehelper;

/**
 * Created by Vc on 2016/12/11.
 */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.jsonResults.Answer;
import com.netlab.vc.coursehelper.util.jsonResults.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class QuestionAdapter extends PagerAdapter {
    private Context context;    //������
    private Question[] questions;
    private HashMap<String, String[]> originAnswers;
    private HashMap<String, Answer> answers;
    private LinkedList<View> viewOptionCache = null;
    private LinkedList<View> viewShortAnswerCache = null;
    private boolean isFinished, hasCompleted;  //一个是是否结束，一个是是否做完

    public QuestionAdapter(Context context, Question[] questions, Answer[] originAnswers, boolean isFinished, boolean hasCompleted) {
        this.context = context;
        this.questions = questions;
        this.isFinished = isFinished;
        this.hasCompleted = hasCompleted;
        this.originAnswers = new HashMap<>();
        if (originAnswers != null) {
            for (int i = 0; i < originAnswers.length; i++) {
                this.originAnswers.put(originAnswers[i].getQuestion_id(), originAnswers[i].getOriginAnswer());
            }
        }
        answers = new HashMap<>();
        viewOptionCache = new LinkedList<>();
        viewShortAnswerCache = new LinkedList<>();
    }

    public Answer[] getAnswers(){
        ArrayList<Answer> allAnswer = new ArrayList<>();
        Iterator iter = answers.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<String, Answer> entry = (HashMap.Entry<String, Answer>) iter.next();
            allAnswer.add(entry.getValue());
        }
        return allAnswer.toArray(new Answer[allAnswer.size()]);
    }
    @Override
    public int getCount() {
        return questions.length;
    }

    @Override

    public Object instantiateItem(ViewGroup container, final int position) {
        ViewHolder viewHolder;
        View convertView = null;

        final Question question = questions[position];

        switch (question.getType()) {
            case "single choice":
            case "multiple choice":
            case "true/false":
                if (viewOptionCache.isEmpty()) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.choice_item,
                            null, false);
                    viewHolder = new ViewHolder(convertView);
                    viewHolder.options = (ListView) convertView.findViewById(R.id.options);
                    convertView.setTag(viewHolder);
                } else {        //������ԭ����View
                    convertView = viewOptionCache.removeFirst();
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.title.setText((position + 1) + "." + question.getContent());
                viewHolder.questionType.setText(Constants.QuestionTypeName.get(question.getType()));
                viewHolder.questionPosition.setText((position + 1) + "/" + questions.length);
                if (!isFinished) {
                    viewHolder.options.setAdapter(new ChoiceAdapter(question, null, null));
                } else {
                    ArrayList<Boolean> states = new ArrayList<>();
                    ArrayList<Boolean> tempCorrects = new ArrayList<>();
                    for (int i = 0; i < question.getOptions().length; i++) {
                        states.add(false);
                        tempCorrects.add(false);
                    }
                    String[] originAnswer = originAnswers.get(question.get_id());
                    if (originAnswer != null) {
                        for (int i = 0; i < originAnswer.length; i++) {
                            String s = originAnswers.get(question.get_id())[i];
                            Integer p = Integer.parseInt(s);
                            states.set(p, true);
                        }
                    }

                    String a = context.getString(R.string.correct_answer);
                    for (int i = 0; i < question.getCorrectAnswer().length; i++) {
                        String s = question.getCorrectAnswer()[i];
                        Integer p = Integer.parseInt(s);
                        a = a + Constants.AnswerTypeName.get(p);
                        tempCorrects.set(p, true);
                    }
                    viewHolder.correctAnswer.setText(a);
                    viewHolder.correctAnswer.setVisibility(View.VISIBLE);
                    viewHolder.options.setAdapter(new ChoiceAdapter(question, states, tempCorrects));   //�����ݴ��ݸ�������
                }
                break;
            case "blank filling":
            case "short answer":
                if (viewShortAnswerCache.isEmpty()) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.short_answer_item,
                            null, false);
                    viewHolder = new ViewHolder(convertView);
                    viewHolder.content = (EditText) convertView.findViewById(R.id.answer);
                    convertView.setTag(viewHolder);
                } else {
                    convertView = viewShortAnswerCache.removeFirst();
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                viewHolder.title.setText((position + 1) + "." + question.getContent());
                viewHolder.questionType.setText(Constants.QuestionTypeName.get(question.getType()));
                viewHolder.questionPosition.setText((position + 1) + "/" + questions.length);
                if (!isFinished) {
                    viewHolder.content.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            Answer result = answers.get(question.get_id());
                            if(result == null){
                                result = new Answer();
                                String[] temp = new String[1];
                                temp[0] = s.toString();
                                result.setOriginAnswer(temp);
                                result.setQuestion_id(question.get_id());
                                answers.put(question.get_id(), result);
                            }else{
                                result.getOriginAnswer()[0] = s.toString();
                            }
                        }
                    });
                } else {
                    String[] originAnswer = originAnswers.get(question.get_id());
                    if (originAnswer != null && originAnswer.length > 0)
                        viewHolder.content.setText(originAnswer[0]);
                    viewHolder.content.setKeyListener(null);
                    String s = context.getString(R.string.correct_answer);
                    s = s + question.getCorrectAnswer()[0];
                    viewHolder.correctAnswer.setText(s);
                    viewHolder.correctAnswer.setVisibility(View.VISIBLE);
                }
                break;
        }

        container.addView(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return convertView;
    }

    @Override
    //����View��׼������
    public void destroyItem(ViewGroup container, int position, Object object) {
        View contentView = (View) object;
        container.removeView(contentView);
        switch (questions[position].getType()) {
            case "single choice":
            case "multiple choice":
            case "true/false":
                this.viewOptionCache.add(contentView);
                break;
            case "blank filling":
            case "short answer":
                this.viewShortAnswerCache.add(contentView);
                break;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //���ڸ���View����
    private class ViewHolder {
        public TextView title;
        public TextView questionType, questionPosition;
        public TextView correctAnswer;
        public ListView options;
        public EditText content;

        public ViewHolder(View convertView) {
            title = (TextView) convertView.findViewById(R.id.title);
            questionType = (TextView) convertView.findViewById(R.id.type);
            questionPosition = (TextView) convertView.findViewById(R.id.position);
            correctAnswer = (TextView) convertView.findViewById(R.id.correct_answer);
        }
    }



    private class ChoiceAdapter extends BaseAdapter {
        private int type;
        private ArrayList<Boolean> states;
        private ArrayList<Boolean> corrects;
        private Question question;
        private int lastCheckedId = 0;

        public ChoiceAdapter(Question question, ArrayList<Boolean> states, ArrayList<Boolean> correctAnswers) {
            this.question = question;
            if (question.getType().equals("multiple choice"))
                type = 1;
            else
                type = 0;
            this.states = states;
            this.corrects = correctAnswers;
            if (states == null)
                recoverStates();
        }

        @Override
        public int getCount() {
            return question.getOptions().length;
        }

        @Override
        public Object getItem(int position) {
            return question.getOptions()[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        private Answer getAnswer() {
            Answer answer = new Answer();
            answer.setQuestion_id(question.get_id());
            ArrayList<String> tempOptions = new ArrayList<>();
            for (int i = 0; i < states.size(); i++) {
                if (states.get(i))
                    tempOptions.add(i + "");
            }
            answer.setOriginAnswer(tempOptions.toArray(new String[tempOptions.size()]));
            return answer;
        }

        private void recoverStates() {
            states = new ArrayList<>();
            for (int i = 0; i < question.getOptions().length; i++)
                states.add(false);
            Answer answer = answers.get(question.get_id());
            if (answer != null && answer.getOriginAnswer() != null) {
                for (int i = 0; i < answer.getOriginAnswer().length; i++) {
                    String s = answer.getOriginAnswer()[i];
                    Integer p = Integer.parseInt(s);
                    states.set(p, true);
                    lastCheckedId = p;
                }
            }
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RadioHolder radioHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.choice_option_item, null);
                radioHolder = new RadioHolder(convertView);
                convertView.setTag(radioHolder);
            } else {
                radioHolder = (RadioHolder) convertView.getTag();
            }
            radioHolder.radio.setChecked(states.get(position));
            radioHolder.item.setText(question.getOptions()[position].getContent());
            if (!isFinished) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (type) {
                            case 0:
                                states.set(lastCheckedId, false);
                                states.set(position, true);
                                lastCheckedId = position;
                                break;
                            case 1:
                                states.set(position, !states.get(position));
                                break;
                        }
                        notifyDataSetChanged();
                        answers.put(question.get_id(), getAnswer());
                    }
                });
            }else{
                if(states.get(position) && corrects.get(position))
                    radioHolder.item.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
                else if(states.get(position) && !corrects.get(position))
                    radioHolder.item.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                else if(!states.get(position) && corrects.get(position))
                    radioHolder.item.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
                else
                    radioHolder.item.setTextColor(context.getResources().getColor(android.R.color.black));
            }
            return convertView;
        }

        //����View����
        private class RadioHolder {
            private RadioButton radio;
            private TextView item;

            public RadioHolder(View view) {
                this.radio = (RadioButton) view.findViewById(R.id.option_button);
                this.item = (TextView) view.findViewById(R.id.option_description);
            }
        }
    }
}


