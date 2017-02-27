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
package com.pedrogomez.renderers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.pedrovgs.renderers.BuildConfig;
import com.pedrogomez.renderers.exception.NullRendererBuiltException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(sdk = 19, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class RendererAdapterTest {

    private static final int ANY_SIZE = 11;
    private static final int ANY_POSITION = 2;
    private static final Object ANY_OBJECT = new Object();
    private static final Object ANY_OTHER_OBJECT = new Object();
    private static final Collection<Object> ANY_OBJECT_COLLECTION = new LinkedList<>();
    private static final int ANY_ITEM_VIEW_TYPE = 3;

    private RendererAdapter<Object> adapter;

    @Mock private RendererBuilder mockedRendererBuilder;
    @Mock private List<Object> mockedCollection;
    @Mock private View mockedConvertView;
    @Mock private ViewGroup mockedParent;
    @Mock private ObjectRenderer mockedRenderer;
    @Mock private View mockedView;
    @Mock private RendererViewHolder mockedRendererViewHolder;
    @Mock private RecyclerView mockedRecyclerView;

    @Before
    public void setUp() throws Exception {
        initializeMocks();
        initializeRendererAdapter();
    }

    @Test
    public void shouldReturnTheCollection() {
        assertEquals(mockedCollection, adapter.getCollection());
    }

    @Test
    public void shouldReturnCollectionSizeOnGetCount() {
        when(mockedCollection.size()).thenReturn(ANY_SIZE);

        assertEquals(ANY_SIZE, adapter.getItemCount());
    }

    @Test
    public void shouldReturnItemAtCollectionPositionOnGetItem() {
        when(mockedCollection.get(ANY_POSITION)).thenReturn(ANY_OBJECT);
    }

    @Test
    public void shouldReturnPositionAsItemId() {
        assertEquals(ANY_POSITION, adapter.getItemId(ANY_POSITION));
    }

    @Test
    public void shouldDelegateIntoRendererBuilderToGetItemViewType() {
        when(mockedCollection.get(ANY_POSITION)).thenReturn(ANY_OBJECT);
        when(mockedRendererBuilder.getItemViewType(ANY_OBJECT)).thenReturn(ANY_ITEM_VIEW_TYPE);

        assertEquals(ANY_ITEM_VIEW_TYPE, adapter.getItemViewType(ANY_POSITION));
    }

    @Test
    public void shouldBuildRendererUsingAllNeededDependencies() {
        when(mockedCollection.get(ANY_POSITION)).thenReturn(ANY_OBJECT);
        when(mockedRendererBuilder.buildRendererViewHolder()).thenReturn(mockedRendererViewHolder);

        adapter.onCreateViewHolder(mockedParent, ANY_ITEM_VIEW_TYPE);

        verify(mockedRendererBuilder).withParent(mockedParent);
        verify(mockedRendererBuilder).withLayoutInflater((LayoutInflater) notNull());
        verify(mockedRendererBuilder).withViewType(ANY_ITEM_VIEW_TYPE);
        verify(mockedRendererBuilder).buildRendererViewHolder();
    }

    @Test
    public void shouldGetRendererFromViewHolderAndCallUpdateRendererExtraValuesOnBind() {
        when(mockedCollection.get(ANY_POSITION)).thenReturn(ANY_OBJECT);
        when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);

        adapter.onBindViewHolder(mockedRendererViewHolder, ANY_POSITION);

        verify(adapter).updateRendererExtraValues(ANY_OBJECT, mockedRenderer, ANY_POSITION);
    }

    @Test
    public void shouldForwardAttachEvent() {
        when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);

        adapter.onViewAttachedToWindow(mockedRendererViewHolder);

        verify(mockedRenderer).onAttached();
    }

    @Test
    public void shouldForwardDetachEvent() {
        when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);

        adapter.onViewDetachedFromWindow(mockedRendererViewHolder);

        verify(mockedRenderer).onDetached();
    }

    @Test
    public void shouldForwardRecycleEvent() {
        when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);

        adapter.onViewRecycled(mockedRendererViewHolder);

        verify(mockedRenderer).onRecycled();
    }

    @Test(expected = NullRendererBuiltException.class)
    public void shouldThrowNullRendererBuiltException() {
        adapter.onCreateViewHolder(mockedParent, ANY_ITEM_VIEW_TYPE);
    }

    @Test
    public void shouldAddElementToCollection() {
        when(mockedCollection.size()).thenReturn(ANY_SIZE);
        adapter.addAndNotify(ANY_OBJECT);

        verify(mockedCollection).add(ANY_OBJECT);
        verify(adapter).notifyItemInserted(ANY_SIZE);
    }

    @Test
    public void shouldAddElementAtPositionToCollection() {
        adapter.addAndNotify(0, ANY_OBJECT);

        verify(mockedCollection).add(0, ANY_OBJECT);
        verify(adapter).notifyItemInserted(0);
    }

    @Test
    public void shouldAddElementAtEndPositionToCollection() {
        when(mockedCollection.size()).thenReturn(ANY_SIZE);
        adapter.addAndNotify(-1, ANY_OBJECT);

        verify(mockedCollection).add(ANY_OBJECT);
        verify(adapter).notifyItemInserted(ANY_SIZE);
    }

    @Test
    public void shouldAddAllElementsToCollection() {
        when(mockedCollection.size()).thenReturn(ANY_SIZE);
        adapter.addAllAndNotify(ANY_OBJECT_COLLECTION);

        verify(mockedCollection).addAll(ANY_OBJECT_COLLECTION);
        verify(adapter).notifyItemRangeInserted(ANY_SIZE, ANY_OBJECT_COLLECTION.size());
    }

    @Test
    public void shouldAddAllElementsAtPositionToCollection() {
        adapter.addAllAndNotify(0, ANY_OBJECT_COLLECTION);

        verify(mockedCollection).addAll(0, ANY_OBJECT_COLLECTION);
        verify(adapter).notifyItemRangeInserted(0, ANY_OBJECT_COLLECTION.size());
    }

    @Test
    public void shouldRemoveElementFromCollection() {
        adapter.remove(ANY_OBJECT);
        verify(mockedCollection).remove(ANY_OBJECT);
    }

    @Test
    public void shouldRemoveElementFromCollection2() {
        when(mockedCollection.indexOf(ANY_OBJECT)).thenReturn(ANY_SIZE);
        adapter.removeAndNotify(ANY_OBJECT);

        verify(mockedCollection).remove(ANY_SIZE);
        verify(adapter).notifyItemRemoved(ANY_SIZE);
    }

    @Test
    public void shouldUpdateElementAtPositionFromCollection() {
        adapter.updateAndNotify(0, ANY_OBJECT);

        verify(mockedCollection).set(0, ANY_OBJECT);
        verify(adapter).notifyItemChanged(0, null);
    }

    @Test
    public void shouldUpdateElementAtPositionFromCollectionAndPassPayload() {
        adapter.updateAndNotify(0, ANY_OBJECT, ANY_OTHER_OBJECT);

        verify(mockedCollection).set(0, ANY_OBJECT);
        verify(adapter).notifyItemChanged(0, ANY_OTHER_OBJECT);
    }

    @Test
    public void shouldMoveElementFromOnePositionToAnotherFromCollection() {
        adapter.moveAndNotify(0, 1, ANY_OBJECT);

        verify(mockedCollection).remove(0);
        verify(mockedCollection).add(1, ANY_OBJECT);
        verify(adapter).notifyItemMoved(0, 1);
    }

    @Test
    public void shouldRemoveAllElementsFromCollection() {
        adapter.removeAllAndNotify(ANY_OBJECT_COLLECTION);

        verify(mockedCollection).removeAll(ANY_OBJECT_COLLECTION);
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void shouldClearElementsFromCollection() {
        adapter.clearAndNotify();

        verify(mockedCollection).clear();
        verify(adapter).notifyDataSetChanged();
    }

    @Test
    public void shouldGetIndexFromCollection() {
        adapter.indexOf(ANY_OBJECT);

        verify(mockedCollection).indexOf(ANY_OBJECT);
    }

    @Test
    public void shouldContainAllItemsInCollection() {
        adapter.containsAll(ANY_OBJECT_COLLECTION);

        verify(mockedCollection).containsAll(ANY_OBJECT_COLLECTION);
    }

    @Test
    public void shouldContainItemInCollection() {
        adapter.contains(ANY_OBJECT);

        verify(mockedCollection).contains(ANY_OBJECT);
    }

    @Test
    public void shouldGetRendererFromViewHolderAndUpdateContentOnBind() {
        when(mockedCollection.get(ANY_POSITION)).thenReturn(ANY_OBJECT);
        when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);

        adapter.onBindViewHolder(mockedRendererViewHolder, ANY_POSITION);

        verify(mockedRenderer).setContent(ANY_OBJECT);
    }

    @Test
    public void shouldGetRendererFromViewHolderAndRenderItOnBind() {
        when(mockedCollection.get(ANY_POSITION)).thenReturn(ANY_OBJECT);
        when(mockedRendererViewHolder.getRenderer()).thenReturn(mockedRenderer);

        adapter.onBindViewHolder(mockedRendererViewHolder, ANY_POSITION);

        verify(mockedRenderer).render(Collections.emptyList());
    }

    @Test(expected = NullRendererBuiltException.class)
    public void shouldThrowExceptionIfNullRenderer() {
        adapter.onBindViewHolder(mockedRendererViewHolder, ANY_POSITION);
    }

    @Test
    public void shouldHookIntoRecyclerView() throws Exception {
        RendererAdapter adapter = new RendererAdapter<>(mockedRendererBuilder);
        adapter.into(mockedRecyclerView);

        verify(mockedRecyclerView).setAdapter(adapter);
    }

    private void initializeMocks() {
        MockitoAnnotations.initMocks(this);
        when(mockedParent.getContext()).thenReturn(RuntimeEnvironment.application);
    }

    private void initializeRendererAdapter() {
        adapter = new RendererAdapter<>(mockedRendererBuilder, mockedCollection);
        adapter = spy(adapter);
    }
}
