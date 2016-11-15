/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedrogomez.renderers.sample.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.pedrogomez.renderers.RendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;
import com.pedrogomez.renderers.RendererContent;
import com.pedrogomez.renderers.sample.R;
import com.pedrogomez.renderers.sample.model.RandomVideoCollectionGenerator;
import com.pedrogomez.renderers.sample.model.Video;
import com.pedrogomez.renderers.sample.ui.renderers.FooterRenderer;
import com.pedrogomez.renderers.sample.ui.renderers.SectionRenderer2;
import com.pedrogomez.renderers.sample.ui.renderers.VideoRenderer;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * AdvancedRecyclerViewActivity for the Renderers demo.
 *
 * @author alberto.ballano
 */
public class ComplexRecyclerViewActivity extends Activity {

    // Let's add less videos so we can see the footer :)
    private static final int VIDEO_COUNT = 3;
    private static final int TYPE_SECTION = 0;
    private static final int TYPE_FOOTER = 1;

    @Bind(R.id.rv_renderers)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Video> videoCollection = RandomVideoCollectionGenerator.generateList(VIDEO_COUNT);

        RendererAdapter adapter = RendererBuilder.create()
              .bind(Video.class, new VideoRenderer())
              .bind(TYPE_FOOTER, new FooterRenderer())
              .bind(TYPE_SECTION, new SectionRenderer2())
              .build()
              .into(recyclerView);

        for (int i = 0, videoCollectionSize = videoCollection.size(); i < videoCollectionSize; i++) {
            adapter.add(new RendererContent<>("Video #" + (i + 1), TYPE_SECTION));
            adapter.add(videoCollection.get(i));
        }
        adapter.add(new RendererContent<>("by Alberto Ballano", TYPE_FOOTER));
    }
}
