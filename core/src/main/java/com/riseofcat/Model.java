package com.riseofcat;
import com.badlogic.gdx.utils.Json;
import com.n8cats.lib_gwt.DefaultValueMap;
import com.n8cats.lib_gwt.LibAllGwt;
import com.n8cats.lib_gwt.Signal;
import com.n8cats.share.ClientPayload;
import com.n8cats.share.Logic;
import com.n8cats.share.Params;
import com.n8cats.share.ServerPayload;
import com.n8cats.share.ShareTodo;
import com.n8cats.share.Tick;
import com.n8cats.share.redundant.ServerSayS;
import com.riseofcat.reflect.Conf;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Model {
public final PingClient<ServerPayload, ClientPayload> client;
@Deprecated public long copyTime;
@Deprecated public long tickTime;
public Logic.Player.Id playerId;
private final DefaultValueMap<Tick, List<Logic.BigAction>> actions = new DefaultValueMap<>(new HashMap<Tick, List<Logic.BigAction>>(), new DefaultValueMap.ICreateNew<List<Logic.BigAction>>() {
	public List<Logic.BigAction> createNew() {return App.context.createConcurrentList();}
});
private final DefaultValueMap<Tick, List<Action>> myActions = new DefaultValueMap<>(new HashMap<Tick, List<Action>>(), new DefaultValueMap.ICreateNew<List<Action>>() {
	public List<Action> createNew() {return new ArrayList<>();}
});
private StateWrapper stable;
private Sync sync;

public static class Sync {
	final float serverTick;
	final float clientTick;
	final long time;
	public Sync(float serverTick, @Nullable Sync oldSync) {
		time = App.timeMs();
		this.serverTick = serverTick;
		if(oldSync == null) this.clientTick = serverTick;
		else this.clientTick = oldSync.calcClientTck();
	}
	private float calcSrvTck(long t) {
		return serverTick + (t - time) / (float)Logic.UPDATE_MS;
	}
	public float calcSrvTck() {
		return calcSrvTck(App.timeMs());
	}
	public float calcClientTck() {
		long t = App.timeMs();
		return calcSrvTck(t) + (clientTick - serverTick) * (1f - LibAllGwt.Fun.arg0toInf(t - time, 600));
	}
}
public Model(Json json, Conf conf) {
	client = new PingClient(json, conf.host, conf.port, "socket", ServerSayS.class);
	client.connect(new Signal.Listener<ServerPayload>() {
		public void onSignal(ServerPayload s) {
			synchronized(this) {
				sync = new Sync(s.tick + client.smartLatencyS / Logic.UPDATE_S, sync);
				if(s.welcome != null) playerId = s.welcome.id;
				if(s.stable != null) {
					if(s.stable.state != null) stable = new StateWrapper(s.stable.state, s.stable.tick);
					else stable.tick(s.stable.tick);
					clearCache(s.stable.tick);
				}
				if(s.actions != null && s.actions.size() > 0) {
					for(ServerPayload.TickActions t : s.actions) {
						actions.getExistsOrPutDefault(new Tick(t.tick)).addAll(t.list);
						clearCache(t.tick + 1);
					}
				}
				for(Tick t : myActions.map.keySet()) {
					Iterator<Action> iterator = myActions.map.get(t).iterator();
					whl:
					while(iterator.hasNext()) {
						Action next = iterator.next();
						if(s.canceled != null) {
							if(s.canceled.contains(next.aid)) {
								iterator.remove();
								clearCache(t.tick + 1);
								continue;
							}
						}
						if(s.apply != null) {
							for(ServerPayload.AppliedActions apply : s.apply) {
								if(apply.aid == next.aid) {
									if(!ShareTodo.SIMPLIFY) actions.getExistsOrPutDefault(t.add(apply.delay)).add(new Logic.PlayerAction(playerId, next.action).toBig());
									iterator.remove();
									clearCache(t.tick + 1);
									continue whl;
								}
							}
						}
					}
				}
			}
		}
	});
}
public String getPlayerName() {
	if(playerId == null) return "Wait connection...";
	return "Player " + playerId.toString();
}
public boolean ready() {
	return playerId != null;
}
private int previousActionId = 0;
public void action(Logic.Action action) {
	synchronized(this) {
		final int clientTick = (int) sync.calcClientTck();//todo +0.5f?
		if(!ready()) return;
		if(false) if(sync.calcSrvTck() - sync.calcClientTck() > Params.DELAY_TICKS * 1.5 || sync.calcClientTck() - sync.calcSrvTck() > Params.FUTURE_TICKS * 1.5) return;
		int w = (int) (client.smartLatencyS / Logic.UPDATE_S + 1);//todo delta serverTick-clientTick
		ClientPayload.ClientAction a = new ClientPayload.ClientAction();
		a.aid = ++previousActionId;
		a.wait = w;
		a.tick = clientTick + w;//todo serverTick?
		a.action = action;
		synchronized(myActions) {
			myActions.getExistsOrPutDefault(new Tick(clientTick + w)).add(new Action(a.aid, a.action));
		}
		ClientPayload payload = new ClientPayload();
		payload.tick = clientTick;
		payload.actions = new ArrayList<>();
		payload.actions.add(a);
		client.say(payload);
	}
}
public void touch(Logic.XY pos) {//todo move out?
	Logic.State displayState = getDisplayState();
	if(displayState == null || playerId == null) return;
	for(Logic.Car car : displayState.cars) {
		if(playerId.equals(car.owner)) {
			Logic.Angle direction = pos.sub(car.pos).calcAngle().add(new Logic.DegreesAngle(0 * 180));
			action(new Logic.Action(direction));
			break;
		}
	}
}
public void update(float graphicDelta) {
//	if(serverTickPreviousTime == null) return;
//	float time = App.timeSinceCreate();
//	serverTick += (time - serverTickPreviousTime) / Logic.UPDATE_S;
//	serverTickPreviousTime = time;
//	clientTick += graphicDelta / Logic.UPDATE_S;
//	clientTick += (serverTick - clientTick) * LibAllGwt.Fun.arg0toInf(Math.abs((serverTick - clientTick) * graphicDelta), 6f);
}
public @Nullable Logic.State getDisplayState() {
	if(sync == null) return null;
	return getState((int) sync.calcClientTck());
}
private StateWrapper cache;
private void clearCache(int tick) {
	if(cache != null && tick < cache.tick) cache = null;
}
private StateWrapper getNearestCache(int tick) {
	if(cache != null && cache.tick <= tick) return cache;
	return null;
}
private void saveCache(StateWrapper value) {
	cache = value;
}
private @Nullable Logic.State getState(int tick) {
	StateWrapper result = getNearestCache(tick);
	Long t = null;
	if(result == null) {
		if(stable == null) return null;
		synchronized(this) {
			result = new StateWrapper(stable);
			saveCache(result);
		}
		t = App.timeMs();
	}
	result.tick(tick);
	if(t != null) tickTime += App.timeMs() - t;
	return result.state;
}
public void dispose() {
	client.close();
}
private class Action extends Logic.PlayerAction {
	public final int aid;
	public Action(int aid, Logic.Action action) {
		this.id = playerId;
		this.action = action;
		this.aid = aid;
	}
}
private class StateWrapper {
	public Logic.State state;
	public int tick;
	public StateWrapper(Logic.State	 state, int tick) {
		this.state = state;
		this.tick = tick;
	}
	public StateWrapper(StateWrapper obj) {
		long t = App.timeMs();
		if(false) state = UtilsCore.copy(obj.state);//todo тяжёлая операция
		else state = new Logic.State(obj.state);
		copyTime += App.timeMs() - t;
		this.tick = obj.tick;
	}
	public void tick(int targetTick) {
		while(tick < targetTick) {
			List<Logic.BigAction> other = actions.map.get(new Tick(tick));
			if(other != null) state.act(other.iterator());
			List<Action> my = myActions.map.get(new Tick(tick));
			if(my != null) {
				synchronized(myActions) {
					state.act(my.iterator());
				}
			}
			state.tick();
			tick++;
		}
	}
}
}
