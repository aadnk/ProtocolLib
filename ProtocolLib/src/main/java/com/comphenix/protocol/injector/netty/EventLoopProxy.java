package com.comphenix.protocol.injector.netty;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelFuture;
import net.minecraft.util.io.netty.channel.ChannelPromise;
import net.minecraft.util.io.netty.channel.EventLoop;
import net.minecraft.util.io.netty.channel.EventLoopGroup;
import net.minecraft.util.io.netty.util.concurrent.EventExecutor;
import net.minecraft.util.io.netty.util.concurrent.ProgressivePromise;
import net.minecraft.util.io.netty.util.concurrent.Promise;
import net.minecraft.util.io.netty.util.concurrent.ScheduledFuture;

/**
 * An event loop proxy.
 * @author Kristian.
 */
abstract class EventLoopProxy implements EventLoop {
	private static final Runnable EMPTY_RUNNABLE = new Runnable() {
		public void run() {
			// Do nothing
		}
	};
	private static final Callable<?> EMPTY_CALLABLE = new Callable<Object>() {
		public Object call() throws Exception {
			return null;
		};
	};

	/**
	 * Retrieve the underlying event loop.
	 * @return The event loop.
	 */
	protected abstract EventLoop getDelegate();
	
	/**
	 * Retrieve a callable that does nothing but return NULL.
	 * @return The empty callable.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Callable<T> getEmptyCallable() {
		return (Callable<T>) EMPTY_CALLABLE;
	}
	
	/**
	 * Retrieve a runnable that does nothing.
	 * @return A NO-OP runnable.
	 */
	public static Runnable getEmptyRunnable() {
		return EMPTY_RUNNABLE;
	}
	
	/**
	 * Invoked when a runnable is being scheduled.
	 * @param runnable - the runnable that is scheduling.
	 * @return The runnable to schedule instead. Cannot be NULL.
	 */
	protected abstract Runnable schedulingRunnable(Runnable runnable);

	/**
	 * Invoked when a callable is being scheduled.
	 * @param runnable - the callable that is scheduling.
	 * @return The callable to schedule instead. Cannot be NULL.
	 */
	protected abstract <T> Callable<T> schedulingCallable(Callable<T> callable);
	
	public void execute(Runnable command) {
		this.getDelegate().execute(schedulingRunnable(command));
	}

	public <T> net.minecraft.util.io.netty.util.concurrent.Future<T> submit(Callable<T> action) {
		return this.getDelegate().submit(schedulingCallable(action));
	}

	public <T> net.minecraft.util.io.netty.util.concurrent.Future<T> submit(Runnable action, T arg1) {
		return this.getDelegate().submit(schedulingRunnable(action), arg1);
	}

	public net.minecraft.util.io.netty.util.concurrent.Future<?> submit(Runnable action) {
		return this.getDelegate().submit(schedulingRunnable(action));
	}
	
	public <V> ScheduledFuture<V> schedule(Callable<V> action, long arg1, TimeUnit arg2) {
		return this.getDelegate().schedule(schedulingCallable(action), arg1, arg2);
	}

	public ScheduledFuture<?> schedule(Runnable action, long arg1, TimeUnit arg2) {
		return this.getDelegate().schedule(schedulingRunnable(action), arg1, arg2);
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable action, long arg1, long arg2, TimeUnit arg3) {
		return this.getDelegate().scheduleAtFixedRate(schedulingRunnable(action), arg1, arg2, arg3);
	}

	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable action, long arg1, long arg2, TimeUnit arg3) {
		return this.getDelegate().scheduleWithFixedDelay(schedulingRunnable(action), arg1, arg2, arg3);
	}
	
	// Boiler plate:
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.getDelegate().awaitTermination(timeout, unit);
	}

	public boolean inEventLoop() {
		return this.getDelegate().inEventLoop();
	}

	public boolean inEventLoop(Thread arg0) {
		return this.getDelegate().inEventLoop(arg0);
	}

	public boolean isShutdown() {
		return this.getDelegate().isShutdown();
	}

	public boolean isTerminated() {
		return this.getDelegate().isTerminated();
	}

	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		return this.getDelegate().invokeAll(tasks);
	}

	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
			TimeUnit unit) throws InterruptedException {
		return this.getDelegate().invokeAll(tasks, timeout, unit);
	}

	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException,
			ExecutionException {
		return this.getDelegate().invokeAny(tasks);
	}

	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return this.getDelegate().invokeAny(tasks, timeout, unit);
	}

	public boolean isShuttingDown() {
		return this.getDelegate().isShuttingDown();
	}

	public Iterator<EventExecutor> iterator() {
		return this.getDelegate().iterator();
	}

	public <V> net.minecraft.util.io.netty.util.concurrent.Future<V> newFailedFuture(Throwable arg0) {
		return this.getDelegate().newFailedFuture(arg0);
	}

	@Override
	public EventLoop next() {
		return this.getDelegate().next();
	}
	
	public <V> ProgressivePromise<V> newProgressivePromise() {
		return this.getDelegate().newProgressivePromise();
	}

	public <V> Promise<V> newPromise() {
		return this.getDelegate().newPromise();
	}

	public <V> net.minecraft.util.io.netty.util.concurrent.Future<V> newSucceededFuture(V arg0) {
		return this.getDelegate().newSucceededFuture(arg0);
	}
	
	public EventLoopGroup parent() {
		return this.getDelegate().parent();
	}

	public ChannelFuture register(Channel arg0, ChannelPromise arg1) {
		return this.getDelegate().register(arg0, arg1);
	}

	public ChannelFuture register(Channel arg0) {
		return this.getDelegate().register(arg0);
	}

	public net.minecraft.util.io.netty.util.concurrent.Future<?> shutdownGracefully() {
		return this.getDelegate().shutdownGracefully();
	}

	public net.minecraft.util.io.netty.util.concurrent.Future<?> shutdownGracefully(long arg0, long arg1, TimeUnit arg2) {
		return this.getDelegate().shutdownGracefully(arg0, arg1, arg2);
	}

	public net.minecraft.util.io.netty.util.concurrent.Future<?> terminationFuture() {
		return this.getDelegate().terminationFuture();
	}
	
	@Deprecated
	public void shutdown() {
		this.getDelegate().shutdown();
	}
	
	@Deprecated
	public List<Runnable> shutdownNow() {
		return this.getDelegate().shutdownNow();
	}
}
