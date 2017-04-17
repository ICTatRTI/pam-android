package org.rti.rcd.researchstack;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PamStepBody <T> implements StepBody {

    private QuestionStep step;
    private StepResult<T[]>    result;
    private PamAnswerFormat format;
    private Choice<T>[]        choices;
    private Set<T> currentSelected;


    public PamStepBody(Step step, StepResult results){
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (PamAnswerFormat) this.step.getAnswerFormat();
        this.choices = format.getChoices();

        // Restore results
        currentSelected = new HashSet<>();

        T[] resultArray = this.result.getResult();
        if(resultArray != null && resultArray.length > 0)
        {
            currentSelected.addAll(Arrays.asList(resultArray));
        }
    }
    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {

        GridView grid = new GridView(parent.getContext());
        grid.setId(View.generateViewId());
        grid.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        grid.setBackgroundColor(Color.WHITE);
        grid.setNumColumns(4);
        grid.setColumnWidth(90);
        grid.setVerticalSpacing(1);
        grid.setHorizontalSpacing(1);
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setGravity(Gravity.CENTER);
        PamImageAdapter adapterImage = new PamImageAdapter(parent.getContext());
        grid.setAdapter(adapterImage);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Log.i(getClass().getName(), "PamStepBody - item clicked in position: "+ position);
                currentSelected.add((T) Integer.valueOf(position));
               adapterImage.setSelectedPosition(position);
            }
        });

        return grid;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if(skipped)
        {
            currentSelected.clear();
            result.setResult((T[]) currentSelected.toArray());
        }
        else
        {
            result.setResult((T[]) currentSelected.toArray());
        }
        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if(currentSelected.isEmpty())
        {
            return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
        }
        else
        {
            return BodyAnswer.VALID;
        }
    }

}
