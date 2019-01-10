/* State declaration */
type state = {
  count: int,
  show: bool,
};

/* Action declaration */
type action =
  | Click
  | Toggle;

/* Component template declaration.
   Needs to be **after** state and action declarations! */
let component = ReasonReact.reducerComponent("Example");

/* greeting and children are props. `children` isn't used, therefore ignored.
   We ignore it by prepending it with an underscore */
let make = (~greeting, _children) => {
  /* spread the other default fields of component here and override a few */
  ...component,

  initialState: () => {count: 0, show: true},

  /* State transitions */
  reducer: (action, state) =>
    switch (action) {
    | Click => ReasonReact.Update({...state, count: state.count + 1})
    | Toggle => ReasonReact.Update({...state, show: !state.show})
    },

  render: self => {
    let message = "You've clicked this " ++ string_of_int(self.state.count) ++ " times(s)";
    <div>
      <MaterialUi.Button onClick={_event => self.send(Click)} color=`Primary variant=`Contained>
        {ReasonReact.string(message)}
      </MaterialUi.Button>
      <MaterialUi.Button onClick={_event => self.send(Toggle)} variant=`Contained>
        {ReasonReact.string("Toggle greeting")}
      </MaterialUi.Button>
      {self.state.show ? ReasonReact.string(greeting) : ReasonReact.null}
    </div>;
  },
};
