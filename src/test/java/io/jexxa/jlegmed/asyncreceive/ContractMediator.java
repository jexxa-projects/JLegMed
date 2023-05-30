package io.jexxa.jlegmed.asyncreceive;


import io.jexxa.jlegmed.asyncreceive.processor.UpdateContract;
import io.jexxa.jlegmed.asyncreceive.processor.PersistContract;
import io.jexxa.jlegmed.asyncreceive.dto.incoming.NewContract;

public final class ContractMediator
{
    /**
     * Example should demonstrate the API how to receive an async message and store it into a database. Database storage is
     * currently seen as some kind of processing.
     */
    public static void main(String[] args)
    {
        var lulli = new Lulli();

        lulli
                .receive(NewContract.class).from("TOPIC").andProcessWith(PersistContract.class)
                .receive(UpdateContract.class).from("TOPIC").andProcessWith(UpdateContract.class)

                .run();
    }

    private ContractMediator()
    {
        //Private constructor since we only offer main
    }


    static class Lulli {
        Lulli receive(Class<?> clazz) {
            return this;
        }

        Lulli from(String topic)
        {
            return this;
        }

        <T> Lulli andProcessWith(Class<T> clazz){
            return this;
        }

        void run(){}

        <T> Lulli and(SinkInterface<T> persistiere) {
            return this;
        }
    }
    @FunctionalInterface
    interface SinkInterface<T>
    {
        public T process(T arg1);
    }

    static class Lulli2 implements SinkInterface<NewContract> {
        @Override
        public NewContract process(NewContract newContract) {
            return newContract;
        }
    }

}
