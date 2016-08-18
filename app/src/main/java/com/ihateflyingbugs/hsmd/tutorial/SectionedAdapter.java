package com.ihateflyingbugs.hsmd.tutorial;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.ihateflyingbugs.hsmd.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class SectionedAdapter extends BaseAdapter {

    public final static int TYPE_SECTION_HEADER = 0;

    public ArrayAdapter<String> headers;
    public Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();

    // 헤더 생성
    public SectionedAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.item_header);
    }

    // 헤더, 섹션 필드 추가 메소드
    public void addSection(String section, Adapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    // 모든 아이템의 수 반환(+ 헤더 필드)
    @Override
    public int getCount() {
        int total = 0;
        for(Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;

        return total;
    }

    @Override
    public Object getItem(int position) {
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // position이 해당 section 안에 있는지 체크
            if(position == 0) return section;
            if(position < size) return adapter.getItem(position - 1);

            // position이 섹션의 크기를 넘어간다면 다음 섹션으로 점프
            position -= size;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionNum = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // position이 해당 section 안에 있는지 체크
            if(position == 0) return headers.getView(sectionNum, convertView, parent);
            if(position < size) return adapter.getView(position - 1, convertView, parent);

            // position이 섹션의 크기를 넘어간다면 다음 섹션으로 점프
            position -= size;
            sectionNum++;
        }
        return null;
    }

    // 뷰 타입의 크기(section의 종류)를 반환
    public int getViewTypeCount() {
        int total = 1;
        for(Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();

        return total;
    }

    // 아이템(필드)의 타입을 반환
    public int getItemViewType(int position) {
        int type = 1;
        for(Object section : this.sections.keySet()) {

            // 해당 섹션의 어댑터와 어댑터의 아이템 크기를 반환
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;  // +1 : header

            // position이 해당 section 안에 있는지 체크
            if(position == 0) return TYPE_SECTION_HEADER;
            if(position < size) return type + adapter.getItemViewType(position - 1);

            // position이 섹션의 크기를 넘어간다면 다음 섹션으로 점프
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    public boolean areAllItemsSectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }
}