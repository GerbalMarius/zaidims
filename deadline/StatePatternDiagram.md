# State Design Pattern - Enemy AI System

## Actual Implementation in Your Project

### High-Level Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                         Enemy                                    │
│                    (Client/Owner)                                │
├─────────────────────────────────────────────────────────────────┤
│ - id: long                                                       │
│ - type: EnemyType                                                │
│ - stateContext: EnemyStateContext                                │
├─────────────────────────────────────────────────────────────────┤
│ + updateAI(players, enemies, checker, server)                    │
│ + getState(): EnemyState                                         │
│ + tryAttack(target, server)                                      │
└──────────────────────┬──────────────────────────────────────────┘
                       │ 1
                       │ has-a
                       │ (Composition)
                       ▼
┌─────────────────────────────────────────────────────────────────┐
│                   EnemyStateContext                              │
│                      (Context)                                   │
├─────────────────────────────────────────────────────────────────┤
│ - currentState: EnemyState                                       │
│ - stateEnteredTime: long                                         │
│ - lastKnownTarget: Player                                        │
│ - CHASE_RANGE: double = 400.0                                    │
│ - ATTACK_RANGE: double = 50.0                                    │
│ - FLEE_HP_THRESHOLD: double = 0.2                                │
├─────────────────────────────────────────────────────────────────┤
│ + setState(newState: EnemyState): void                           │
│ + update(enemy, players, enemies, checker, server): void         │
│ + getTimeInCurrentState(): long                                  │
│ + getChaseRange(): double                                        │
│ + getAttackRange(): double                                       │
└──────────────────────┬──────────────────────────────────────────┘
                       │ 1
                       │ delegates to / aggregates
                       │ ◇────────────────────────┐
                       ▼ 1                        │
            ┌──────────────────────────┐          │
            │    «interface»           │          │
            │      EnemyState          │          │
            │   (State Interface)      │          │
            ├──────────────────────────┤          │
            │                          │          │
            ├──────────────────────────┤          │
            │ + update(context,        │          │
            │     enemy, players,      │          │
            │     enemies, checker,    │          │
            │     server): void        │          │
            │ + getStateName(): String │          │
            │ + getAllowedTransitions()│          │
            │   : Set<Class>           │          │
            └──────────┬───────────────┘          │
                       │                          │
                       │ implements               │
                       │ (Realization) ╱╲         │
         ┌─────────────┼──────────────┼───────────┼───────────┬──────────────┐
         │             │              │           │           │              │
         ▼             ▼              ▼           ▼           ▼              ▼
┌──────────────┐┌──────────────┐┌──────────────┐┌─────────────┐┌──────────────┐┌──────────────┐
│  IdleState   ││  ChaseState  ││PatrolState   ││FleeState    ││FollowLeader  ││ZigZagChase   │
│              ││              ││              ││             ││State         ││State         │
│(Singleton)   ││(Singleton)   ││(Multi-inst)  ││(Singleton)  ││(Multi-inst)  ││(Singleton)   │
├──────────────┤├──────────────┤├──────────────┤├─────────────┤├──────────────┤├──────────────┤
│- INSTANCE    ││- INSTANCE    ││- pointIndex  ││- INSTANCE   ││- leader      ││- INSTANCE    │
│- IDLE_       ││- LOSE_       ││- forward     ││- RECOVERY_  ││- offsetX     ││- LOSE_       │
│  DURATION_MS ││  INTEREST_   ││- patrolPoints││  HP_        ││- offsetY     ││  INTEREST_   │
│              ││  RANGE       ││              ││  THRESHOLD  ││              ││  RANGE       │
│              ││              ││              ││             ││              ││- ZIGZAG_WIDTH│
├──────────────┤├──────────────┤├──────────────┤├─────────────┤├──────────────┤├──────────────┤
│+ update()    ││+ update()    ││+ update()    ││+ update()   ││+ update()    ││+ update()    │
│+ getName()   ││+ getName()   ││+ getName()   ││+ getName()  ││+ getName()   ││+ getName()   │
│+ getAllowed  ││+ getAllowed  ││+ getAllowed  ││+ getAllowed ││+ getAllowed  ││+ getAllowed  │
│  Transitions ││  Transitions ││  Transitions ││  Transitions││  Transitions ││  Transitions │
│              ││              ││              ││             ││              ││              │
│Returns:      ││Returns:      ││Returns:      ││Returns:     ││Returns:      ││Returns:      │
│{ChaseState,  ││{IdleState,   ││{ChaseState,  ││{IdleState}  ││{IdleState,   ││{IdleState,   │
│ PatrolState} ││ FleeState,   ││ IdleState}   ││             ││ ChaseState}  ││ FleeState}   │
│              ││ ZigZagChase} ││              ││             ││              ││              │
└──────┬───────┘└──────┬───────┘└──────┬───────┘└─────┬───────┘└──────┬───────┘└──────┬───────┘
       │               │               │              │                │                │
       └───────────────┴───────────────┴──────────────┴────────────────┴────────────────┘
                                       │
                                       │ may call context.setState()
                                       │ to transition (Dependency)
                                       └──────────────────────────────────────────┘
```

## Relationships Explained

### 1. **Enemy → EnemyStateContext (Composition)**
   - **Type**: Composition (strong ownership)
   - **Multiplicity**: 1-to-1 (Each Enemy has exactly one EnemyStateContext)
   - **Direction**: Enemy owns and manages EnemyStateContext lifecycle
   - **Code**: `private EnemyStateContext stateContext = new EnemyStateContext();`
   - **Purpose**: Enemy delegates all AI behavior to its state context

### 2. **EnemyStateContext → EnemyState (Aggregation)**
   - **Type**: Aggregation (weak association)
   - **Multiplicity**: 1-to-1 (Context has one active State at a time)
   - **Direction**: Context holds a reference to the current State object
   - **Code**: `private EnemyState currentState;`
   - **Purpose**: Context delegates update() calls to the current state

### 3. **EnemyState ← Concrete States (Realization)**
   - **Type**: Interface implementation (Realization)
   - **Multiplicity**: 1-to-6 (one interface, six implementations)
   - **Direction**: Each concrete state implements the EnemyState interface
   - **Code**: `public final class IdleState implements EnemyState`
   - **Purpose**: All states must implement update(), getStateName(), and getAllowedTransitions()

### 4. **Concrete States → EnemyStateContext (Dependency)**
   - **Type**: Dependency (method parameter)
   - **Direction**: States receive context as parameter in update()
   - **Code**: `void update(EnemyStateContext context, ...)`
   - **Purpose**: States can trigger transitions via `context.setState(newState)`

### 5. **Concrete States ⇄ Concrete States (Controlled Transitions)**
   - **Type**: Dependency with validation
   - **Direction**: States can transition to specific allowed states only
   - **Validation**: `getAllowedTransitions()` returns allowed target states
   - **Example**: 
     - IdleState can → {ChaseState, PatrolState}
     - ChaseState can → {IdleState, FleeState, ZigZagChaseState}
   - **Purpose**: Ensures valid state machine transitions

## State Transition Diagram

```
                    ┌──────────────────────────────────────────┐
                    │                                          │
                    │              IdleState                   │
                    │        (Initial/Default State)           │
                    │                                          │
                    │  - Random wandering                      │
                    │  - Checks for nearby players             │
                    │                                          │
                    └────┬───────────────────────────┬─────────┘
                         │ player in range           │
                         │ (400 units)               │
                         ▼                           ▼
          ┌──────────────────────┐         ┌─────────────────────┐
          │    ChaseState        │         │   PatrolState       │
          │                      │         │                     │
          │  - Pursue player     │         │  - Follow waypoints │
          │  - Direct movement   │         │  - Check for players│
          │                      │         │                     │
          └──┬────────────┬──────┘         └──────┬──────────────┘
             │            │                       │
             │            └───────────────────────┘
             │                     │
             │ low HP              │ player out of range
             │ (< 20%)             │ (> 700 units)
             ▼                     │
    ┌─────────────────┐            │
    │   FleeState     │            │
    │                 │            │
    │  - Run away     │            │
    │  - 1.5x speed   │            │
    │                 │            │
    └────────┬────────┘            │
             │                     │
             │ HP recovered        │
             │ (> 50%)             │
             └─────────────────────┘
                         │
                         ▼
                  ┌──────────────────┐
                  │   IdleState      │
                  └──────────────────┘


              ┌──────────────────────┐         ┌──────────────────────┐
              │ ZigZagChaseState     │         │ FollowLeaderState    │
              │                      │         │                      │
              │  - Evasive chase     │         │  - Formation follow  │
              │  - Zigzag pattern    │         │  - Maintain offset   │
              │  - Hard to hit       │         │  - Group behavior    │
              │                      │         │                      │
              └──────────────────────┘         └──────────────────────┘
                         │                              │
                         └──────────┬───────────────────┘
                                    │ player out of range
                                    │ or leader dead
                                    ▼
                             ┌──────────────┐
                             │  IdleState   │
                             └──────────────┘
```

## Allowed Transitions (Enforced by Code)

```
IdleState           → {ChaseState, PatrolState}
ChaseState          → {IdleState, FleeState, ZigZagChaseState}
PatrolState         → {ChaseState, IdleState}
FleeState           → {IdleState}
FollowLeaderState   → {IdleState, ChaseState}
ZigZagChaseState    → {IdleState, FleeState}
```

## Collaboration Sequence (Example: Idle → Chase Transition)

```
   Enemy        EnemyStateContext    IdleState         ChaseState      Player
     │                │                   │                 │            │
     │  updateAI()    │                   │                 │            │
     ├───────────────>│                   │                 │            │
     │                │  update(enemy,    │                 │            │
     │                │    players,...)   │                 │            │
     │                ├──────────────────>│                 │            │
     │                │                   │                 │            │
     │                │                   │ getClosestPlayer()           │
     │                │                   ├─────────────────────────────>│
     │                │                   │                 │            │
     │                │                   │ calculate distance           │
     │                │                   ├──────────┐      │            │
     │                │                   │          │      │            │
     │                │                   │<─────────┘      │            │
     │                │                   │                 │            │
     │                │                   │ [distance <= 400]            │
     │                │                   │                 │            │
     │                │  setState(        │                 │            │
     │                │    ChaseState)    │                 │            │
     │                │<──────────────────┤                 │            │
     │                │                   │                 │            │
     │                │ [validate transition]               │            │
     │                │ [allowed: IdleState → ChaseState]   │            │
     │                │ [currentState = ChaseState]         │            │
     │                │ [stateEnteredTime = now()]          │            │
     │                │                   │                 │            │
     │  next frame    │                   │                 │            │
     ├───────────────>│                   │                 │            │
     │                │  update(enemy,    │                 │            │
     │                │    players,...)   │                 │            │
     │                ├──────────────────────────────────────>           │
     │                │                   │                 │            │
     │                │                   │  update(context,│            │
     │                │                   │    enemy,...)   │            │
     │                │                   │                 │            │
     │                │                   │   performChase()│            │
     │                │                   │   ┌─────────────┤            │
     │  move          │                   │   │             │            │
     │<───────────────┼───────────────────┼───┘             │            │
     │                │                   │                 │            │
```

## Key Connection Types Summary:

1. **Composition (Enemy ●─ EnemyStateContext)**: Enemy "owns" EnemyStateContext, strong lifecycle dependency
2. **Aggregation (EnemyStateContext ◇─ EnemyState)**: Context "has-a" State, State can exist independently
3. **Realization (ConcreteState ╱╲ EnemyState)**: ConcreteStates implement/realize the EnemyState interface
4. **Dependency (ConcreteState ⇢ Context)**: ConcreteStates depend on Context for state transitions
5. **Validated Transitions**: getAllowedTransitions() enforces valid state machine rules

## Code Examples from Your Project

### State Interface Definition
```java
public interface EnemyState {
    void update(EnemyStateContext context, Enemy enemy, Collection<Player> players,
                Map<Long, Enemy> allEnemies, CollisionChecker checker, Server server);
    
    String getStateName();
    
    default Set<Class<? extends EnemyState>> getAllowedTransitions() {
        return Collections.emptySet();
    }
}
```

### Context Managing State Transitions with Validation
```java
public void setState(EnemyState newState) {
    if (this.currentState == newState) {
        return;
    }
    Set<Class<? extends EnemyState>> allowed = currentState.getAllowedTransitions();
    if (!allowed.isEmpty()) {
        boolean isAllowed = false;
        for (Class<? extends EnemyState> allowedClass : allowed) {
            if (allowedClass.isInstance(newState)) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) {
            System.err.println(String.format(
                "Invalid state transition from %s to %s - using fallback",
                currentState.getStateName(),
                newState.getStateName()
            ));
            return;
        }
    }
    this.currentState = newState;
    this.stateEnteredTime = System.currentTimeMillis();
}
```

### Concrete State Implementation Example (IdleState)
```java
public final class IdleState implements EnemyState {
    private static final IdleState INSTANCE = new IdleState();
    
    public static IdleState getInstance() { return INSTANCE; }
    
    @Override
    public void update(EnemyStateContext context, Enemy enemy, 
                      Collection<Player> players, ...) {
        // Check for nearby players -> Chase
        Player nearest = enemy.getClosestPlayer(players);
        if (nearest != null) {
            double dist = Math.hypot(
                nearest.getGlobalX() - enemy.getGlobalX(),
                nearest.getGlobalY() - enemy.getGlobalY()
            );
            if (dist <= context.getChaseRange()) {
                context.setState(ChaseState.getInstance()); // Trigger transition
                return;
            }
        }
        performRandomMovement(enemy, allEnemies, checker);
    }
    
    @Override
    public Set<Class<? extends EnemyState>> getAllowedTransitions() {
        return Set.of(ChaseState.class, PatrolState.class);
    }
}
```

### Enemy Using the State Context
```java
public abstract class Enemy extends Entity {
    private EnemyStateContext stateContext = new EnemyStateContext();
    
    public EnemyState getState() {
        return stateContext.getCurrentState();
    }
    
    public void updateAI(Collection<Player> players, Map<Long, Enemy> allEnemies, 
                        CollisionChecker checker, Server server) {
        stateContext.update(this, players, allEnemies, checker, server);
    }
}
```

## Benefits of This Implementation:

1. **Loose Coupling**: Enemy doesn't know about concrete states, only the State interface
2. **Open/Closed Principle**: Can add new states (e.g., DefendState, RetreatState) without modifying existing code
3. **Single Responsibility**: Each ConcreteState handles its own behavior (IdleState handles wandering, ChaseState handles pursuit)
4. **Validated State Transitions**: getAllowedTransitions() prevents invalid transitions (e.g., FleeState can't go directly to PatrolState)
5. **Singleton Pattern**: Most states are singletons (memory efficient), except states with instance data (PatrolState, FollowLeaderState)
6. **Testability**: Each state can be tested independently
7. **Maintainability**: AI behavior changes are isolated to specific state classes



